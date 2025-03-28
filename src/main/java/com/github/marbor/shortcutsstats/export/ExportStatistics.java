package com.github.marbor.shortcutsstats.export;

import com.github.marbor.shortcutsstats.model.Shortcut;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportStatistics {
    private static final Logger log = Logger.getInstance(ExportStatistics.class);

    public void export(File file, List<Shortcut> shortcuts) {
        try (FileWriter out = new FileWriter(file)) {
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.Builder.create().setHeader("shortcut", "description", "count").build())) {
                shortcuts.forEach(s -> {
                    try {
                        printer.printRecord(s.getShortcut(), s.getDescription(), s.getCount());
                    } catch (IOException e) {
                        log.error("Error when converting shortcut statistics to the CSV format", e);
                    }
                });
            }
        } catch (IOException e) {
            log.error("Error when exporting shortcut statistics to the file", e);
        }
    }
}
