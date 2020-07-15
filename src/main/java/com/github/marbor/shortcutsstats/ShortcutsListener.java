package com.github.marbor.shortcutsstats;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.keymap.KeymapUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ShortcutsListener implements AnActionListener {
    private final ShortcutsStatistics shortcutsStatistics = ServiceManager.getService(ShortcutsStatistics.class);

    public void afterActionPerformed(@NotNull AnAction action, @NotNull DataContext dataContext, @NotNull AnActionEvent event) {
        Optional.ofNullable(KeymapUtil.getEventCallerKeystrokeText(event))
                .ifPresent(shortcutsStatistics::addShortcutUsage);
    }
}
