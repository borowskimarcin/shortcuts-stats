package com.github.marbor.shortcutsstats;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Map;
import java.util.function.ToLongFunction;

import static java.util.Comparator.comparingLong;

public class StatsToolWindow implements OnStatisticsChangeListener {
    private final ShortcutsStatistics shortcutsStatistics = ServiceManager.getService(ShortcutsStatistics.class);
    private JPanel myToolWindowContent;
    private JTable statsTable;
    private JButton resetButton;
    private JLabel totalLabel;

    public StatsToolWindow(ToolWindow toolWindow) {
        updateTable();
        shortcutsStatistics.register(this);
        resetButton.addActionListener((e) -> {
            shortcutsStatistics.resetStatistic();
            updateTable();
        });
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    @Override
    public void onChange() {
        updateTable();
    }

    private void updateTable() {
        final long total = shortcutsStatistics.getStatistics()
                .values()
                .stream()
                .mapToLong(l -> l)
                .sum();

        final Object[][] stats = shortcutsStatistics.getStatistics().entrySet()
                .stream()
                .sorted(comparingLong((ToLongFunction<Map.Entry<String, Long>>) Map.Entry::getValue).reversed())
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .toArray(Object[][]::new);

        statsTable.setModel(new StatsTableModel(stats));
        totalLabel.setText("Total: " + total);
    }
}

class StatsTableModel extends AbstractTableModel {
    private final Object[][] data;
    private final String[] columns;

    StatsTableModel(Object[][] data) {
        this.data = data;
        this.columns = new String[]{"Shortcut", "Count"};
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}