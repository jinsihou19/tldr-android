package com.jinsihou.tldr.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface HistoryDAO {

    @Query("SELECT * FROM history order by uid desc limit 10")
    LiveData<List<HistoryEntity>> find();


    @Query("SELECT * FROM history WHERE timestamp BETWEEN :from AND :to")
    LiveData<List<HistoryEntity>> findHistoryBetweenDates(Date from, Date to);

    @Insert
    void insert(HistoryEntity historyEntity);

}
