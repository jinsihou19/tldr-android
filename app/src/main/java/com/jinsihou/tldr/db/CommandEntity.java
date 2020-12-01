package com.jinsihou.tldr.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.jinsihou.tldr.data.Command;

@Entity(
        indices = {@Index(value = {"name", "platform", "lang"}, unique = true),
                @Index(value = {"name", "platform"})},
        tableName = "commands"
)
public class CommandEntity {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "platform")
    public String platform;

    @ColumnInfo(name = "lang")
    public String lang;

    @ColumnInfo(name = "text")
    public String text;

    @Ignore
    public CommandEntity() {
    }

    public CommandEntity(String name, String platform, String lang) {
        this.name = name;
        this.platform = platform;
        this.lang = lang;
        this.text = "";
    }

    public String getName() {
        return name;
    }

    public CommandEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getPlatform() {
        return platform;
    }

    public CommandEntity setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public CommandEntity setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getText() {
        return text;
    }

    public CommandEntity setText(String text) {
        this.text = text;
        return this;
    }

    public Command toCommand() {
        return new Command(name, text, platform, lang);
    }
}
