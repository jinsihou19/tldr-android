package com.jinsihou.tldr;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.jinsihou.tldr.repository.CommandRepository;

import java.util.Date;

public class SyncWorker extends Worker {
    public static final long DAY = 24 * 60 * 60 * 1000L;
    private final CommandRepository dataRepository;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dataRepository = ((BasicApplication) getApplicationContext()).getDataRepository();
    }

    @NonNull
    @Override
    public Result doWork() {
        long updateTime = dataRepository.getUpdateTime();
        if (new Date().getTime() - updateTime > 7 * DAY) {
            dataRepository.syncTask();
        }

        return Result.success();
    }
}
