package org.sitoolkit.wt.infra.csv;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;

import com.opencsv.CSVWriter;

public class CsvFileWriter {

    public void write(List<List<String>> data, String absolutePath) {

        List<String[]> allLines = data.stream().map(list -> list.toArray(new String[list.size()]))
                .collect(Collectors.toList());

        writeFile(allLines, absolutePath);

    }

    private void writeFile(List<String[]> allLines, String absolutePath) {

        try (OutputStream out = new FileOutputStream(absolutePath);
                CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            boolean applyQuotesToAll = false;
            writer.writeAll(allLines, applyQuotesToAll);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
