package com.bc.dectree.verification;

import java.io.*;
import java.util.*;

class DataFrame {
    static class Series {
        final String name;
        final List<Object> data;

        public Series(String name) {
            this.name = name;
            this.data = new ArrayList<>();
        }
    }

    final List<String> columnNames;
    final Map<String, Series> series;
    private int numRows;

    static DataFrame readCsv(String filePath) throws IOException {
        try (LineNumberReader r = new LineNumberReader(new FileReader(filePath))) {
            String line;
            List<String> columnNames = new ArrayList<>();
            DataFrame dataFrame = null;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (!(line.isEmpty() || line.startsWith("#"))) {
                    String[] parts = line.split("\t");
                    if (dataFrame == null) {
                        for (int columnIndex = 0; columnIndex < parts.length; columnIndex++) {
                            String part = parts[columnIndex];
                            String columnName = part.trim();
                            if (columnNames.contains(columnName)) {
                                throw new IOException(String.format("%s(%s): invalid CSV format, column %s, column names must be unique: \"%s\"",
                                                                    filePath, r.getLineNumber(), columnIndex + 1, columnName));
                            }
                            columnNames.add(columnName);
                        }
                        dataFrame = new DataFrame(columnNames);
                    } else {
                        if (columnNames.size() != parts.length) {
                            throw new IOException(String.format("%s(%s): row %s: invalid CSV format, expected %d value(s), found %s",
                                                                filePath, r.getLineNumber(), dataFrame.getNumRows() + 1, columnNames.size(), parts.length));
                        }
                        Object[] row = new Object[columnNames.size()];
                        for (int columnIndex = 0; columnIndex < parts.length; columnIndex++) {
                            Object value;
                            if (parts[columnIndex].isEmpty()) {
                                value = null;
                            } else {
                                try {
                                    value = Double.valueOf(parts[columnIndex]);
                                } catch (NumberFormatException e) {
                                    value = parts[columnIndex];
                                }
                            }
                            row[columnIndex] = value;
                        }
                        dataFrame.addRow(row);
                    }
                }
            }

            return dataFrame;
        }
    }


    void writeCsv(String filePath) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath))) {
            for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
                if (columnIndex > 0) {
                    w.write("\t");
                }
                w.write(columnNames.get(columnIndex));
            }
            w.write("\n");
            for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
                for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
                    if (columnIndex > 0) {
                        w.write("\t");
                    }
                    w.write(String.valueOf(getValue(columnIndex, rowIndex)));
                }
                w.write("\n");
            }
        }
    }

    DataFrame(String[] columnNames) {
        this(Arrays.asList(columnNames));
    }

    DataFrame(List<String> columnNames) {
        this.columnNames = columnNames;
        this.series = new LinkedHashMap<>();
        for (String columnName : columnNames) {
            Series series = new Series(columnName);
            this.series.put(series.name, series);
        }
        numRows = 0;
    }

    public int getNumRows() {
        return numRows;
    }

    Object getValue(int columnIndex, int rowIndex) {
        return getValue(columnNames.get(columnIndex), rowIndex);
    }

    Object getValue(String columnName, int rowIndex) {
        return series.get(columnName).data.get(rowIndex);
    }

    public double getDouble(String columnName, int rowIndex) {
        return (double) series.get(columnName).data.get(rowIndex);
    }

    void addRow(double[] row) {
        for (int i = 0; i < row.length; i++) {
            String columnName = columnNames.get(i);
            series.get(columnName).data.add(row[i]);
        }
        numRows++;
    }

    void addRow(Object[] row) {
        for (int i = 0; i < row.length; i++) {
            String columnName = columnNames.get(i);
            series.get(columnName).data.add(row[i]);
        }
        numRows++;
    }

    void sortValues(String byColumnName) {
        Series bySeries = this.series.get(byColumnName);
        assert bySeries != null;
        List<Object> byData = bySeries.data;
        Integer[] indexes = new Integer[numRows];
        for (int i = 0; i < numRows; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, (i1, i2) -> {
            Comparable c1 = (Comparable) byData.get(i1);
            Comparable c2 = (Comparable) byData.get(i2);
            return c1.compareTo(c2);
        });
        for (String columnName : columnNames) {
            List<Object> data = this.series.get(columnName).data;
            List<Object> oldData = new ArrayList<>(data);
            for (int i = 0; i < indexes.length; i++) {
                Integer index = indexes[i];
                data.set(i, oldData.get(index));
            }
            // if (columnName.equals(byColumnName)) {
            //     System.out.println("data = " + data);
            // }
        }
    }

}
