package org.sitoolkit.wt.infra.csv;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;

public class CsvFileReader {

    public List<Map<String, String>> read(String absolutePath, boolean headerRowOnly) {

        List<String[]> data = readFile(absolutePath, headerRowOnly);

        String[] firstRow = data.iterator().next();
        List<String> header = Stream.of(firstRow).map(this::cleansing).collect(Collectors.toList());

        return data.stream().map(rowData -> toRowMap(rowData, header)).collect(Collectors.toList());
    }

    private Map<String, String> toRowMap(String[] rowData, List<String> header) {

        Collector<Integer, ?, Map<String, String>> toMap = Collectors.toMap(
                index -> header.get(index), index -> rowData[index], (h1, h2) -> h1,
                LinkedHashMap<String, String>::new);

        return IntStream.range(0, header.size()).boxed().collect(toMap);

    }

    private List<String[]> readFile(String absolutePath, boolean headerRowOnly) {

        try (InputStream in = new BOMInputStream(new FileInputStream(absolutePath));
                CSVReader reader = new CSVReader(new InputStreamReader(in))) {

            if (headerRowOnly) {
                return Collections.singletonList(reader.readNext());
            } else {
                return reader.readAll();
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String cleansing(String str) {
        return StringUtils.isEmpty(str) ? "" : str.replaceAll("[\\r\\n 　]", "");
    }

}
