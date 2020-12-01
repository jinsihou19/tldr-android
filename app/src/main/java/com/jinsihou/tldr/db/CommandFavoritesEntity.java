package com.jinsihou.tldr.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.jinsihou.tldr.data.Command;

import java.util.Date;

@Entity(
        primaryKeys = {"name", "platform"},
        tableName = "command_favorites"
)
public class CommandFavoritesEntity {

    @ColumnInfo
    @NonNull
    public String name;

    @ColumnInfo
    @NonNull
    public String platform;

    @ColumnInfo
    public Date timestamp;

    @Ignore
    public CommandFavoritesEntity(Command.Index index) {
        this(index.name, index.platform);
        this.timestamp = new Date();
    }

    public CommandFavoritesEntity(@NonNull String name, @NonNull String platform) {
        this.name = name;
        this.platform = platform;
    }
}
