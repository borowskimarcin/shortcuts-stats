package com.github.marbor.shortcutsstats;

import com.github.marbor.shortcutsstats.actions.ExportAction;
import com.github.marbor.shortcutsstats.actions.ResetAction;
import com.github.marbor.shortcutsstats.model.Shortcut;
import com.github.marbor.shortcutsstats.model.ShortcutView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.ToolWindow;
import org.apache.commons.lang3.StringUtils;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static com.github.marbor.shortcutsstats.ShortcutsListener.UNKNOWN_SHORTCUT_DESCRIPTION;
import static com.github.marbor.shortcutsstats.TextUtils.makeHugeNumberShorter;
import static java.util.Optional.ofNullable;

public class StatsToolWindow implements Observer {
    private final ShortcutsStatistics shortcutsStatistics = ApplicationManager.getApplication().getService(ShortcutsStatistics.class);
    private JPanel myToolWindowContent;
    private JLabel totalLabel;
    private JScrollPane shortcutsPanel;
    private JList<ShortcutView> shortcutsList;
    private JLabel descriptionLabel;
    private JPanel descriptionPanel;
    private JPanel toolBarPanel;

    public StatsToolWindow(ToolWindow toolWindow) {
        updateView();
        shortcutsStatistics.register(this);
        shortcutsList.addListSelectionListener(this::showDescription);
        descriptionPanel.setVisible(false);
        totalLabel.setIcon(AllIcons.Actions.GroupByModuleGroup);
        descriptionLabel.setIcon(AllIcons.Actions.IntentionBulb);
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    @Override
    public void onChange() {
        updateView();
    }

    private void updateView() {
        final DefaultListModel<ShortcutView> model = new DefaultListModel<>();

        shortcutsStatistics.getShortcuts()
                .stream()
                .map(s -> new ShortcutView(getDisplayText(s), getDescription(s)))
                .forEach(model::addElement);

        shortcutsList.setModel(model);
        totalLabel.setText("Total: " + shortcutsStatistics.getShortcutsNumber() + " shortcuts used " + makeHugeNumberShorter(shortcutsStatistics.getTotal()) + " times.");
    }

    private void showDescription(javax.swing.event.ListSelectionEvent e) {
        if (e.getValueIsAdjusting() || shortcutsList.getSelectedValue() == null) {
            return;
        }
        descriptionPanel.setVisible(true);
        final String shortcutDescription = ofNullable(shortcutsList.getSelectedValue().getDescription()).orElse("");

        if (StringUtils.isBlank(shortcutDescription)) {
            this.descriptionLabel.setEnabled(false);
            this.descriptionLabel.setText("N/A - description of the shortcut will appear after next usage.");
        } else if (UNKNOWN_SHORTCUT_DESCRIPTION.equals(shortcutDescription)) {
            this.descriptionLabel.setEnabled(false);
            this.descriptionLabel.setText(shortcutDescription);
        } else {
            this.descriptionLabel.setEnabled(true);
            this.descriptionLabel.setText(shortcutDescription);
        }
    }

    private String getDisplayText(Shortcut shortcut) {
        return String.format("<html>%d - <b>%s</b> (%s)</html>", shortcut.count(), shortcut.shortcut(), shortcut.description());
    }

    private String getDescription(Shortcut shortcut) {
        final String description = shortcut.description();
        return description != null ? description : "";
    }

    private void createUIComponents() {
        toolBarPanel = new JPanel();
        DefaultActionGroup actions = new DefaultActionGroup();
        final ExportAction exportAction = new ExportAction();
        final ResetAction resetAction = new ResetAction();
        actions.add(exportAction);
        actions.add(resetAction);
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("shortcuts-toolbar", actions, true);
        toolbar.setTargetComponent(toolBarPanel);
        toolBarPanel.add(toolbar.getComponent());
    }
}

