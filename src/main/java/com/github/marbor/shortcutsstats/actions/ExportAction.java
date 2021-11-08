package com.github.marbor.shortcutsstats.actions;

import com.github.marbor.shortcutsstats.ShortcutsStatistics;
import com.github.marbor.shortcutsstats.export.ExportStatistics;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.jetbrains.annotations.NotNull;

public class ExportAction extends AnAction {
    private final ShortcutsStatistics shortcutsStatistics = ServiceManager.getService(ShortcutsStatistics.class);
    private final ExportStatistics exportStatistics = new ExportStatistics();

    public ExportAction() {
        super("Export Shortcuts to File", "Export shortcuts to file", AllIcons.Actions.Menu_saveall);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final FileSaverDescriptor singleFileDescriptor = new FileSaverDescriptor("Export Shortcuts", "Export shortcuts", "csv");
        final FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(singleFileDescriptor, e.getProject());
        final VirtualFileWrapper virtualFileWrapper = saveFileDialog.save(null, "shortcuts.csv");
        if (virtualFileWrapper != null) {
            exportStatistics.export(virtualFileWrapper.getFile(), shortcutsStatistics.getShortcuts());
        }
    }
}
