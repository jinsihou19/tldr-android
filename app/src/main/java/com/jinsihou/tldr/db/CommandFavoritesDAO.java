package com.jinsihou.tldr.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.jinsihou.tldr.data.Command;

import java.util.List;

@Dao
public interface CommandFavoritesDAO {

    @Query("select count(0) from command_favorites where name=:name and platform=:platform")
    LiveData<Integer> findCount(String name, String platform);

    @Query("select name,platform from command_favorites order by timestamp DESC")
    LiveData<List<Command.Index>> list();

    @Insert
    void insert(CommandFavoritesEntity commandFavoritesEntity);

    @Delete
    void delete(CommandFavoritesEntity commandFavoritesEntity);
}
