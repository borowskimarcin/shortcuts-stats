package com.github.marbor.shortcutsstats.actions;

import com.github.marbor.shortcutsstats.ShortcutsStatistics;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class ResetAction extends AnAction {
    private final ShortcutsStatistics shortcutsStatistics = ServiceManager.getService(ShortcutsStatistics.class);

    public ResetAction() {
        super("Reset Shortcuts", "Reset shortcuts", AllIcons.Actions.Cancel);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (Messages.showYesNoDialog(
                "Are you sure you would like to remove shortcuts statistics?",
                "Remove Statistics",
                Messages.getQuestionIcon()) == Messages.YES) {
            shortcutsStatistics.resetStatistic();
        }
    }
}
