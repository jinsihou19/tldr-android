package com.jinsihou.tldr.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.jinsihou.tldr.data.Command;

import java.util.Date;

@Entity(tableName = "history")
public class HistoryEntity {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String platform;

    @ColumnInfo
    public Date timestamp;


    @Ignore
    public HistoryEntity(Command.Index index) {
        this(index.name, index.platform);
        this.timestamp = new Date();
    }

    public HistoryEntity(String name, String platform) {
        this.name = name;
        this.platform = platform;
    }
}
