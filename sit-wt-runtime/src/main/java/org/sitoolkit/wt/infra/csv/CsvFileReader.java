package org.sitoolkit.wt.infra.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.opencsv.CSVReader;

public class CsvFileReader {

    public List<String[]> read(String absolutePath, boolean headerRowOnly) {

        try (CSVReader reader = new CSVReader(new FileReader(absolutePath))) {

            if (headerRowOnly) {
                return Collections.singletonList(reader.readNext());
            } else {
                return reader.readAll();
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
