package com.jinsihou.tldr;

import android.app.Application;

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