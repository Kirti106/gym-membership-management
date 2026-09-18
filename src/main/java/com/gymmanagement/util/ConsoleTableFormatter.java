package com.gymmanagement.util;

import java.util.ArrayList;
import java.util.List;

public class ConsoleTableFormatter {

    private final List<String> headers = new ArrayList<>();
    private final List<List<String>> rows = new ArrayList<>();

    public ConsoleTableFormatter(String... headers) {
        for (String h : headers) {
            this.headers.add(h);
        }
    }

    public void addRow(String... cells) {
        List<String> row = new ArrayList<>();
        for (String c : cells) {
            row.add(c == null ? "" : c);
        }
        rows.add(row);
    }

    public void print() {
        if (headers.isEmpty()) {
            return;
        }

        int[] colWidths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            colWidths[i] = headers.get(i).length();
        }

        for (List<String> row : rows) {
            for (int i = 0; i < row.size() && i < colWidths.length; i++) {
                if (row.get(i).length() > colWidths[i]) {
                    colWidths[i] = row.get(i).length();
                }
            }
        }

        StringBuilder lineSeparator = new StringBuilder("+");
        for (int width : colWidths) {
            lineSeparator.append("-".repeat(width + 2)).append("+");
        }

        System.out.println(lineSeparator);

        // Header Row
        StringBuilder headerLine = new StringBuilder("|");
        for (int i = 0; i < headers.size(); i++) {
            headerLine.append(String.format(" %-" + colWidths[i] + "s |", headers.get(i)));
        }
        System.out.println(headerLine);
        System.out.println(lineSeparator);

        // Data Rows
        if (rows.isEmpty()) {
            int totalWidth = lineSeparator.length() - 4;
            System.out.println("| " + centerString("No records found", totalWidth) + " |");
            System.out.println(lineSeparator);
            return;
        }

        for (List<String> row : rows) {
            StringBuilder rowLine = new StringBuilder("|");
            for (int i = 0; i < headers.size(); i++) {
                String cell = i < row.size() ? row.get(i) : "";
                rowLine.append(String.format(" %-" + colWidths[i] + "s |", cell));
            }
            System.out.println(rowLine);
        }

        System.out.println(lineSeparator);
    }

    private String centerString(String text, int len) {
        if (text.length() >= len) return text.substring(0, len);
        int padding = (len - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(len - text.length() - padding);
    }
}
