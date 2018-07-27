package org.sitoolkit.wt.infra.csv;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CsvFileReaderTest {

    private String[] expectedHeaderData = new String[] { "No.", "項目名", "操作", "ロケーター\n形式", "ロケーター",
            "データ形式", "スクリーン\nショット", "ブレーク\nポイント", "ケース_001", "ケース_002" };

    private String[] expectedFooterData = new String[] { "16", "名前", "verify", "xpath",
            "//*[@id=\"confirm\"]/tbody/tr[1]/td", "", "前", "", "試験太郎", "試験花子" };

    @Test
    public void test() {
        CsvFileReader target = new CsvFileReader();
        List<String[]> result = target.read("testscript/CsvTestScript.csv", false);
        assertThat(result.size(), is(17));
        assertThat(result.get(0), is(expectedHeaderData));
        assertThat(result.get(16), is(expectedFooterData));
    }

    @Test
    public void testHeaderOnly() {
        CsvFileReader target = new CsvFileReader();
        List<String[]> result = target.read("testscript/CsvTestScript.csv", true);
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(expectedHeaderData));
    }

    @Test(expected = IllegalStateException.class)
    public void testException() {
        CsvFileReader target = new CsvFileReader();

        @SuppressWarnings("unused")
        List<String[]> result = target.read("illegalPath", false);
    }

}
