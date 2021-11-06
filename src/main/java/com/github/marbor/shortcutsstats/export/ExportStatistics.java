package com.github.marbor.shortcutsstats.export;

import com.github.marbor.shortcutsstats.model.Shortcut;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportStatistics {
    public void export(File file, List<Shortcut> shortcuts) {
        try (FileWriter out = new FileWriter(file)) {
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.Builder.create().setHeader("name", "description", "count").build())) {
                shortcuts.forEach(s -> {
                    try {
                        printer.printRecord(s.getName(), s.getDescription(), s.getCount());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
