package org.sitoolkit.wt.infra.csv;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Test;

public class CsvFileReaderTest {

    private String[] columns = new String[] { "No.", "項目名", "操作", "ロケーター形式", "ロケーター", "データ形式",
            "スクリーンショット", "ブレークポイント", "ケース_001", "ケース_002" };

    private String[] expectedHeaderData = new String[] { "No.", "項目名", "操作", "ロケーター\n形式", "ロケーター",
            "データ形式", "スクリーン\nショット", "ブレーク\nポイント", "ケース_001", "ケース_002" };

    private String[] expectedFooterData = new String[] { "16", "名前", "verify", "xpath",
            "//*[@id=\"confirm\"]/tbody/tr[1]/td", "", "前", "", "試験太郎", "試験花子" };

    private Map<String, String> expectedHeader = new LinkedHashMap<>();
    private Map<String, String> expectedFooter = new LinkedHashMap<>();

    public CsvFileReaderTest() {

        IntStream.range(0, expectedHeaderData.length).forEachOrdered(i -> {
            expectedHeader.put(columns[i], expectedHeaderData[i]);
            expectedFooter.put(columns[i], expectedFooterData[i]);
        });

    }

    @Test
    public void test() {
        CsvFileReader target = new CsvFileReader();
        List<Map<String, String>> result = target.read("testscript/CsvTestScript.csv", false);
        assertThat(result.size(), is(17));
        assertThat(result.get(0), is(expectedHeader));
        assertThat(result.get(16), is(expectedFooter));
    }

    @Test
    public void testHeaderOnly() {
        CsvFileReader target = new CsvFileReader();
        List<Map<String, String>> result = target.read("testscript/CsvTestScript.csv", true);
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(expectedHeader));
    }

    @Test(expected = IllegalStateException.class)
    public void testException() {
        CsvFileReader target = new CsvFileReader();

        @SuppressWarnings("unused")
        List<Map<String, String>> result = target.read("illegalPath", false);
    }

}
