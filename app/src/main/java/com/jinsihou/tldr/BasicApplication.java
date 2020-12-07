package com.jinsihou.tldr;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.jinsihou.tldr.db.AppDatabase;
import com.jinsihou.tldr.repository.CommandRepository;

/**
 * Android Application class. Used for accessing singletons.
 */
public class BasicApplication extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean autoSync = preferences.getBoolean("auto_sync", false);
        if (autoSync) {
            WorkRequest syncWorkRequest = new OneTimeWorkRequest
                    .Builder(SyncWorker.class)
                    .build();
            WorkManager.getInstance(this).enqueue(syncWorkRequest);
        }

    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public AppExecutors getAppExecutors() {
        return mAppExecutors;
    }

    public CommandRepository getDataRepository() {
        return CommandRepository.getInstance(this, mAppExecutors);
    }
}