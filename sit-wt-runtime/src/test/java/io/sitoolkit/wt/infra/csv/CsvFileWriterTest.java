package io.sitoolkit.wt.infra.csv;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.sitoolkit.wt.app.config.BaseConfig;
import io.sitoolkit.wt.infra.PropertyManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BaseConfig.class)
public class CsvFileWriterTest {

    @Resource
    CsvFileWriter target;

    @Resource
    PropertyManager pm;

    List<List<String>> data = new ArrayList<>();

    @Test
    public void test() throws IOException {
        Charset encoding = StandardCharsets.UTF_8;
        boolean useBOM = true;

        pm.setCsvCharset(encoding);
        pm.setCsvHasBOM(useBOM);

        Path actualPath = Paths.get("target/CsvWriteTestActual.csv");
        target.write(data, actualPath.toAbsolutePath().toString());
        Path expectedPath = Paths.get("testdata/csv/CsvWriteTestExpectedBom.csv");

        verifyCsv(actualPath, expectedPath, encoding);
        assertThat(hasBOM(actualPath), is(useBOM));
    }

    @Test
    public void testNoBom() throws IOException {
        Charset encoding = StandardCharsets.UTF_8;
        boolean useBOM = false;

        pm.setCsvCharset(encoding);
        pm.setCsvHasBOM(useBOM);

        Path actualPath = Paths.get("target/CsvWriteTestActual.csv");
        target.write(data, actualPath.toAbsolutePath().toString());
        Path expectedPath = Paths.get("testdata/csv/CsvWriteTestExpectedNoBom.csv");

        verifyCsv(actualPath, expectedPath, encoding);
        assertThat(hasBOM(actualPath), is(useBOM));
    }

    @Test
    public void testSJIS() throws IOException {
        Charset encoding = Charset.forName("Windows-31j");
        boolean useBOM = false;

        pm.setCsvCharset(encoding);
        pm.setCsvHasBOM(useBOM);

        Path actualPath = Paths.get("target/CsvWriteTestActual.csv");
        target.write(data, actualPath.toAbsolutePath().toString());
        Path expectedPath = Paths.get("testdata/csv/CsvWriteTestExpectedSJIS.csv");

        verifyCsv(actualPath, expectedPath, encoding);
        assertThat(hasBOM(actualPath), is(useBOM));
    }

    @Before
    public void setup() {
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
    }

    public void verifyCsv(Path actualPath, Path expectedPath, Charset encoding) throws IOException {
        List<String> actualAllLines = FileUtils.readLines(actualPath.toFile(), encoding);
        Iterator<String> actualItr = actualAllLines.iterator();

        List<String> expectedAllLines = FileUtils.readLines(expectedPath.toFile(), encoding);

        assertThat(actualAllLines.size(), is(expectedAllLines.size()));

        for (Iterator<String> expectedItr = expectedAllLines.iterator(); expectedItr.hasNext();) {
            assertThat(actualItr.next(), is(expectedItr.next()));
        }
    }

    public boolean hasBOM(Path actualPath) throws IOException {
        byte[] actualBOM = Arrays.copyOfRange(Files.readAllBytes(actualPath), 0, 3);
        return Arrays.equals(actualBOM, ByteOrderMark.UTF_8.getBytes());
    }
}
