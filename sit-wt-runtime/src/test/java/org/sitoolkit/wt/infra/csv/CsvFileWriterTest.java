package org.sitoolkit.wt.infra.csv;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class CsvFileWriterTest {

    @Test
    public void test() throws IOException {

        CsvFileWriter target = new CsvFileWriter();

        List<List<String>> data = new ArrayList<>();

        data.add(Arrays.asList("正常データ", "12", "13", "14", "15"));
        data.add(Arrays.asList("途中列改行データ", "22", "2\n3", "24", "25"));
        data.add(Arrays.asList("最終列改行データ", "32", "33", "34", "3\n5"));
        data.add(Arrays.asList("最終列空白データ", "42", "43", "", ""));
        data.add(Arrays.asList("", "先頭列最終列空白データ", "53", "54", ""));
        data.add(Arrays.asList("先頭列\n改行データ", "62", "63", "64", "65"));
        data.add(Arrays.asList("真ん中列改行データ、最終列空白データ", "72", "7\n3", "74", ""));
        data.add(Arrays.asList("先頭列\n複数改行\nデータ", "82", "83", "84", "85"));
        data.add(Arrays.asList("先頭列,カンマ,あり,データ", "92", "93", "94", "95"));
        data.add(Arrays.asList("先頭列,カンマ,あり,データ", "92", "", "", ""));
        data.add(Arrays.asList("ダブルクォートあり", "最後だけ\"", "\"真\"ん中\"", "\"最初だけ", "真中\"だけ"));
        data.add(Arrays.asList("ダブルクォートだけ", "\"", "\"\"", "\"\"\"", "\"\"\"\""));

        Path actualPath = Paths.get("target/CsvWriteTestActual.csv");

        target.write(data, actualPath.toAbsolutePath().toString());

        List<String> actualAllLines = FileUtils.readLines(actualPath.toFile(),
                StandardCharsets.UTF_8);
        Iterator<String> actualItr = actualAllLines.iterator();

        Path expectedPath = Paths.get("testdata/csv/CsvWriteTestExpected.csv");
        List<String> expectedAllLines = FileUtils.readLines(expectedPath.toFile(),
                StandardCharsets.UTF_8);

        assertThat(actualAllLines.size(), is(expectedAllLines.size()));

        for (Iterator<String> expectedItr = expectedAllLines.iterator(); expectedItr.hasNext();) {
            assertThat(actualItr.next(), is(expectedItr.next()));
        }

    }

}
