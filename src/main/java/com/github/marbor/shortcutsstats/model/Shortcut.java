package com.github.marbor.shortcutsstats.model;

public class Shortcut {
    private final String name;
    private final String description;
    private final Long count;

    public Shortcut(String name, String description, Long count) {
        this.name = name;
        this.description = description;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getCount() {
        return count;
    }
}
