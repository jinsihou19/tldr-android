package com.jinsihou.tldr.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jinsihou.tldr.data.Command;

import java.util.List;
import java.util.Set;

@Dao
public interface CommandDAO {

    @Query("SELECT count(distinct name) FROM commands")
    int count();

    /**
     * 获取所有的平台
     *
     * @return 支持的平台
     */
    @Query("SELECT distinct platform FROM commands")
    LiveData<List<String>> getPlatforms();

    @Query("SELECT name,platform FROM commands where platform IN (:platformFilters) group by name,platform")
    LiveData<List<Command.Index>> queryCommandIndex(Set<String> platformFilters);

    @Query("SELECT * FROM commands WHERE name == :name and (lang == 'en' or lang == :local)")
    LiveData<List<CommandEntity>> findByName(String name, String local);

    @Query("SELECT * FROM commands WHERE name == :name and platform=:platform and (lang == 'en' or lang == :local)")
    LiveData<List<CommandEntity>> findOnPlatform(String name, String platform, String local);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CommandEntity... commandEntity);

}