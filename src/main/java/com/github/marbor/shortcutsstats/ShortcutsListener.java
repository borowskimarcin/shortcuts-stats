package com.github.marbor.shortcutsstats;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.keymap.KeymapUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Optional;

public class ShortcutsListener implements AnActionListener {
    public static final String UNKNOWN_SHORTCUT_DESCRIPTION = "N/A - unknown shortcut description";
    private final ShortcutsStatistics shortcutsStatistics = ServiceManager.getService(ShortcutsStatistics.class);

    public void afterActionPerformed(@NotNull AnAction action, @NotNull DataContext dataContext, @NotNull AnActionEvent event) {
        getEventCallerKeystrokeText(event)
                .ifPresent(shortcut -> shortcutsStatistics.addShortcutUsage(shortcut, getShortcutDescription(action)));
    }

    private String getShortcutDescription(@NotNull AnAction action) {
        return Optional.ofNullable(action.getTemplateText())
                .orElse(UNKNOWN_SHORTCUT_DESCRIPTION);
    }

    // This is replacement for the KeymapUtil.getEventCallerKeystrokeText(event) that will be deprecated
    public static Optional<String> getEventCallerKeystrokeText(@NotNull AnActionEvent event) {
        if (event.getInputEvent() instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) event.getInputEvent();
            return Optional.of(KeymapUtil.getKeystrokeText(KeyStroke.getKeyStroke(ke.getKeyCode(), ke.getModifiersEx())));
        }

        return Optional.empty();
    }
}
