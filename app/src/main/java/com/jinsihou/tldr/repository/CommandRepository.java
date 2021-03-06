package com.jinsihou.tldr.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.jinsihou.tldr.AppExecutors;
import com.jinsihou.tldr.BasicApplication;
import com.jinsihou.tldr.data.Command;
import com.jinsihou.tldr.db.AppDatabase;
import com.jinsihou.tldr.db.AppInfoDAO;
import com.jinsihou.tldr.db.AppInfoEntity;
import com.jinsihou.tldr.db.CommandDAO;
import com.jinsihou.tldr.db.CommandEntity;
import com.jinsihou.tldr.db.CommandFavoritesDAO;
import com.jinsihou.tldr.db.CommandFavoritesEntity;
import com.jinsihou.tldr.db.HistoryDAO;
import com.jinsihou.tldr.db.HistoryEntity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommandRepository {
    public static final String ONLINE_INDEX_URL = "https://tldr.sh/assets/index.json";
    public static final String ZIP_URL = "https://tldr.sh/assets/tldr.zip";
    public static final String ZIP_FILENAME = "tldr.zip";
    private static CommandRepository INSTANCE;
    private final Application application;
    private final AppExecutors executors;
    private final CommandDAO commandDAO;
    private final HistoryDAO historyDAO;
    private final CommandFavoritesDAO commandFavoritesDAO;
    private final AppInfoDAO appInfoDAO;

    private CommandRepository(Application application, AppExecutors executors) {
        AppDatabase appDatabase = ((BasicApplication) application).getDatabase();
        this.application = application;
        this.executors = executors;
        this.commandDAO = appDatabase.commandDao();
        this.historyDAO = appDatabase.historyDAO();
        this.commandFavoritesDAO = appDatabase.commandFavoritesDAO();
        this.appInfoDAO = appDatabase.appInfoDAO();
    }

    public static CommandRepository getInstance(Application application, AppExecutors executors) {
        if (INSTANCE == null) {
            synchronized (CommandRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CommandRepository(application, executors);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * ??????????????????????????????
     *
     * @return ????????????????????????
     */
    public LiveData<List<Command.Index>> queryCommandIndex(Set<String> platformFilters) {
        syncAndInitData();
        return commandDAO.queryCommandIndex(platformFilters);
    }

    /**
     * ???????????????????????????
     *
     * @param index ????????????
     * @return ??????????????????
     */
    public LiveData<Command> queryCommandByIndex(Command.Index index) {
        return queryCommand(commandDAO.findOnPlatform(index.name, index.platform, Locale.getDefault().getLanguage()));
    }

    /**
     * ???????????????????????????
     *
     * @param name ????????????
     * @return ??????????????????
     */
    public LiveData<Command> queryCommandByName(String name) {
        return queryCommand(commandDAO.findByName(name, Locale.getDefault().getLanguage()));
    }

    private LiveData<Command> queryCommand(LiveData<List<CommandEntity>> source) {
        MediatorLiveData<Command> commandMediatorLiveData = new MediatorLiveData<>();
        String language = Locale.getDefault().getLanguage();
        commandMediatorLiveData.addSource(source,
                commandEntities -> {
                    if (commandEntities.size() == 0) {
                        commandMediatorLiveData.setValue(Command.EMPTY);
                    } else if (commandEntities.size() == 1) {
                        Command command = commandEntities.get(0).toCommand();
                        commandMediatorLiveData.setValue(command);
                        addHistory(command.getIndex());
                    } else {
                        commandEntities.stream()
                                .filter(commandEntity -> language.equals(commandEntity.getLang()))
                                .map(CommandEntity::toCommand)
                                .findFirst()
                                .ifPresent(cmd -> {
                                    commandMediatorLiveData.setValue(cmd);
                                    addHistory(cmd.getIndex());
                                });
                    }
                });
        return commandMediatorLiveData;
    }

    /**
     * ????????????????????????
     *
     * @param name     ????????????
     * @param platform ??????
     * @return ???????????????
     */
    public LiveData<Command> queryCommand(String name, String platform) {
        MediatorLiveData<Command> commandMediatorLiveData = new MediatorLiveData<>();
        String language = Locale.getDefault().getLanguage();
        commandMediatorLiveData.addSource(commandDAO.findOnPlatform(name, platform, language), commandEntities -> {
            if (commandEntities.size() == 0) {
                commandMediatorLiveData.setValue(Command.EMPTY);
            } else {
                commandEntities.stream()
                        .filter(commandEntity -> language.equals(commandEntity.getLang()))
                        .map(CommandEntity::toCommand)
                        .findFirst()
                        .ifPresent(cmd -> {
                            commandMediatorLiveData.setValue(cmd);
                            addHistory(cmd.getIndex());
                        });
            }
        });
        return commandMediatorLiveData;
    }

    /**
     * ????????????????????????
     *
     * @param index ????????????
     */
    private void addHistory(Command.Index index) {
        executors.diskIO().execute(() -> historyDAO.insert(new HistoryEntity(index)));
    }

    /**
     * ??????????????????
     *
     * @param index ??????????????????
     */
    public void like(Command.Index index) {
        executors.diskIO().execute(() -> commandFavoritesDAO.insert(new CommandFavoritesEntity(index)));
    }

    /**
     * ????????????????????????
     *
     * @param index ??????????????????
     */
    public void unlike(Command.Index index) {
        executors.diskIO().execute(() -> commandFavoritesDAO.delete(new CommandFavoritesEntity(index)));
    }

    public LiveData<List<Command.Index>> getFavorites() {
        return commandFavoritesDAO.list();
    }

    /**
     * ??????????????????
     *
     * @param index ??????????????????
     * @return ??????
     */
    public LiveData<Boolean> isLike(Command.Index index) {
        MediatorLiveData<Boolean> isLikeLiveData = new MediatorLiveData<>();
        isLikeLiveData.addSource(
                commandFavoritesDAO.findCount(index.name, index.platform),
                count -> {
                    isLikeLiveData.setValue(count != null && count > 0);
                });
        return isLikeLiveData;
    }

    public void loadCommandIndexOnline() {
        executors.networkIO().execute(() -> {
            int value = commandDAO.count();
            if (value != 0) {
                return;
            }
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(ONLINE_INDEX_URL)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                JSONObject index = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray commands = index.getJSONArray("commands");
                List<CommandEntity> list = new ArrayList<>();
                for (int i = 0; i < commands.length(); i++) {
                    JSONObject c = commands.getJSONObject(i);
                    JSONArray targets = c.getJSONArray("targets");
                    for (int j = 0; j < targets.length(); j++) {
                        JSONObject t = targets.getJSONObject(j);
                        list.add(new CommandEntity(c.getString("name"), t.getString("os"), t.getString("language")));
                    }
                }
                commandDAO.insertAll(list.toArray(new CommandEntity[0]));
            } catch (IOException | JSONException e) {
                Log.e("online", "CommandRepository Request Exception", e);
            }
        });
    }

    /**
     * ??????????????????
     */
    public long getUpdateTime() {
        AppInfoEntity info = appInfoDAO.getInfo();
        if (info != null) {
            return info.timestamp.getTime();
        }

        return 0;
    }

    /**
     * ???????????????
     */
    public LiveData<Boolean> isLoad() {
        MediatorLiveData<Boolean> load = new MediatorLiveData<>();
        load.addSource(appInfoDAO.get(), appInfoEntity
                -> load.setValue(AppInfoEntity.STATE.valueOf(appInfoEntity.state)
                == AppInfoEntity.STATE.LOADING));
        return load;
    }

    /**
     * ?????????????????????
     */
    public void syncAndInitData() {
        executors.networkIO().execute(() -> {
            int value = commandDAO.count();
            if (value != 0) {
                return;
            }
            syncTask();
        });
    }

    public void syncTask() {
        appInfoDAO.insert(AppInfoEntity.state(AppInfoEntity.STATE.LOADING));
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(ZIP_URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                InputStream is = null;
                try {
                    is = Objects.requireNonNull(response.body()).byteStream();
                    File zip = new File(application.getCacheDir(), ZIP_FILENAME);
                    ByteStreams.copy(is, new FileOutputStream(zip));
                    ZipFile zipFile = new ZipFile(zip, ZipFile.OPEN_READ);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    List<CommandEntity> list = new ArrayList<>();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (entry.isDirectory()) {
                            continue;
                        }
                        String name = entry.getName();
                        if (!name.endsWith(".md")) {
                            continue;
                        }
                        CommandEntity commandEntity = new CommandEntity();
                        String[] split = name.split("/");
                        if (split.length < 3) {
                            continue;
                        }
                        if ("pages".equals(split[0])) {
                            commandEntity.setLang("en");
                        } else {
                            commandEntity.setLang(split[0].substring(6));
                        }
                        try (InputStreamReader in = new InputStreamReader(zipFile.getInputStream(entry))) {
                            commandEntity
                                    .setName(split[2].substring(0, split[2].length() - 3))
                                    .setPlatform(split[1])
                                    .setText(CharStreams.toString(in));
                            list.add(commandEntity);
                        }
                    }
                    commandDAO.insertAll(list.toArray(new CommandEntity[0]));
                    appInfoDAO.insert(AppInfoEntity.state(AppInfoEntity.STATE.SUCCESS));
                } catch (Throwable e) {
                    appInfoDAO.insert(AppInfoEntity.state(AppInfoEntity.STATE.FAILED));
                    Log.e("sync", "????????????", e);
                } finally {
                    application.getCacheDir().deleteOnExit();
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        Log.e("sync", "????????????", e);
                    }
                }
            }
        });
    }

}
