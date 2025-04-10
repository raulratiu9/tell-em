package com.tellem.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVLoggerService {

    private static final String FILE_NAME = "benchmark_results.csv";

    public static void log(String databaseType, String operation, int index, long timeMs) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.append(String.format("%s,%s,%d,%d,%s\n", databaseType, operation, index, timeMs, timestamp));
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }
}

