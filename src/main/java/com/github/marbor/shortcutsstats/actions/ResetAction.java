package com.github.marbor.shortcutsstats.actions;

import com.github.marbor.shortcutsstats.ShortcutsStatistics;
import com.intellij.icons.AllIcons;
import com.intellij.icons.ExpUiIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.IconManager;
import org.jetbrains.annotations.NotNull;

public class ResetAction extends AnAction {
    private final ShortcutsStatistics shortcutsStatistics = ApplicationManager.getApplication().getService(ShortcutsStatistics.class);

    public ResetAction() {
        super("Reset Shortcuts Stats", "Reset shortcuts stats", ExpUiIcons.General.Delete);
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
