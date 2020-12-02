package com.jinsihou.tldr.data;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令对象
 */
public class Command {
    public static final Command EMPTY = new Command("", "");
    public static final String[] COLUMNS_INDEX = {"name", "platform"};

    public String name;

    public String platform = "common";

    public String lang = "en";

    public String text;

    public Command() {
    }

    public Command(String name) {
        this.name = name;
    }

    public Command(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public Command(String name, String text, String platform, String lang) {
        this.name = name;
        this.text = text;
        this.platform = platform;
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 命令的索引信息
     */
    public static class Index {
        public String name;

        public String platform;

        public Index(String name, String platform) {
            this.name = name;
            this.platform = platform;
        }

        public Map<String, String> toMap() {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("platform", platform);
            return map;
        }

        public String[] toStringArray() {
            return new String[]{name, platform};
        }
    }

    public Index getIndex() {
        return new Index(name, platform);
    }
}
