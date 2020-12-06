package com.jinsihou.tldr.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AppInfoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppInfoEntity appInfoEntities);

    @Query("SELECT * FROM app_info limit 1")
    LiveData<AppInfoEntity> get();
}
