package com.jinsihou.tldr.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.jinsihou.tldr.AppExecutors;

@Database(entities = {CommandEntity.class, HistoryEntity.class, CommandFavoritesEntity.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "commands-db";
    private static AppDatabase sInstance;

    public abstract CommandDAO commandDao();

    public abstract HistoryDAO historyDAO();

    public abstract CommandFavoritesDAO commandFavoritesDAO();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                }
            }
        }
        return sInstance;
    }

    private static AppDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .build();
    }

}
