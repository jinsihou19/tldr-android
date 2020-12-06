package com.jinsihou.tldr.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity(tableName = "app_info")
public class AppInfoEntity {
    public static final String LOADING = "loading";
    public static final String SUCCESS = "success";
    public static final String FAILED = "failed";
    public static final String TLDR = "TLDR";

    public enum STATE {
        LOADING, SUCCESS, FAILED
    }

    @PrimaryKey
    @NonNull
    public String content;

    @ColumnInfo
    public String state;

    @ColumnInfo
    public Date timestamp;

    public AppInfoEntity(String state, @NotNull String content, Date timestamp) {
        this.state = state;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static AppInfoEntity state(STATE state) {
        return new AppInfoEntity(state.toString(), TLDR, new Date());
    }
}
