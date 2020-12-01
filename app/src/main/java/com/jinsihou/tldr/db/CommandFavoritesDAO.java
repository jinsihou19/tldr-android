package com.jinsihou.tldr.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CommandFavoritesDAO {

    @Query("select count(0) from command_favorites where name=:name and platform=:platform")
    LiveData<Integer> findCount(String name, String platform);

    @Insert
    void insert(CommandFavoritesEntity commandFavoritesEntity);

    @Delete
    void delete(CommandFavoritesEntity commandFavoritesEntity);
}
