package com.github.marbor.shortcutsstats.model;

public class ShortcutView {
    private final String displayText;
    private final String description;

    public ShortcutView(String displayText, String description) {
        this.displayText = displayText;
        this.description = description;
    }

    @Override
    public String toString() {
        return displayText;
    }

    public String getDescription() {
        return description;
    }
}
