package com.github.marbor.shortcutsstats.model;

public class Shortcut {
    private final String shortcut;
    private final String description;
    private final Long count;

    public Shortcut(String shortcut, String description, Long count) {
        this.shortcut = shortcut;
        this.description = description;
        this.count = count;
    }

    public String getShortcut() {
        return shortcut;
    }

    public String getDescription() {
        return description;
    }

    public Long getCount() {
        return count;
    }
}
