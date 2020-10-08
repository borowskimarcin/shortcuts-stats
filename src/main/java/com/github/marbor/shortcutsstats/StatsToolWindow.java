package com.github.marbor.shortcutsstats;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Map;
import java.util.function.ToLongFunction;

import static com.github.marbor.shortcutsstats.ShortcutsListener.UNKNOWN_SHORTCUT_DESCRIPTION;
import static com.github.marbor.shortcutsstats.TextUtils.makeHugeNumberShorter;
import static com.github.marbor.shortcutsstats.TextUtils.timeOrTimes;
import static java.util.Comparator.comparingLong;
import static java.util.Optional.ofNullable;

public class StatsToolWindow implements Observer {
    private final ShortcutsStatistics shortcutsStatistics = ServiceManager.getService(ShortcutsStatistics.class);
    private JPanel myToolWindowContent;
    private JButton resetButton;
    private JLabel totalLabel;
    private JScrollPane shortcutsPanel;
    private JList<ShortcutView> shortcutsList;
    private JLabel descriptionLabel;
    private JPanel descriptionPanel;

    public StatsToolWindow(ToolWindow toolWindow) {
        updateView();
        shortcutsStatistics.register(this);
        resetButton.addActionListener((e) -> resetStats());
        shortcutsList.addListSelectionListener(this::showDescription);
        descriptionPanel.setVisible(false);
        totalLabel.setIcon(AllIcons.Actions.GroupByModuleGroup);
        resetButton.setIcon(AllIcons.Actions.Cancel);
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
        final long total = shortcutsStatistics.getTotal();
        final DefaultListModel<ShortcutView> model = new DefaultListModel<>();

        shortcutsStatistics.getStatistics()
                .entrySet()
                .stream()
                .sorted(comparingLong((ToLongFunction<Map.Entry<String, Long>>) Map.Entry::getValue).reversed())
                .map(e -> new ShortcutView(getDisplayText(e), getDescription(e.getKey())))
                .forEach(model::addElement);

        shortcutsList.setModel(model);
        totalLabel.setText("Total: " + shortcutsStatistics.getStatistics().size() + " shortcuts used " + makeHugeNumberShorter(total) + " times.");
    }

    private void showDescription(javax.swing.event.ListSelectionEvent e) {
        if (e.getValueIsAdjusting() || shortcutsList.getSelectedValue() == null) {
            return;
        }

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

    private String getDisplayText(java.util.Map.Entry<String, Long> e) {
        return e.getKey() + " pressed " + e.getValue() + " " + timeOrTimes(e.getValue());
    }

    private String getDescription(String shortcut) {
        final String description = shortcutsStatistics.getShortcutDescription().get(shortcut);
        return description != null ? description : "";
    }

    private void resetStats() {
        if (Messages.showYesNoDialog(
                "Are you sure you would like to remove shortcuts statistics?",
                "Remove Statistics",
                Messages.getQuestionIcon()) == Messages.YES) {
            shortcutsStatistics.resetStatistic();
        }
    }
}

class ShortcutView {
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