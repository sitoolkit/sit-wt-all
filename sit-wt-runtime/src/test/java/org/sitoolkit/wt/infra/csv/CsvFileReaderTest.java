package org.sitoolkit.wt.infra.csv;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sitoolkit.wt.app.config.BaseConfig;
import org.sitoolkit.wt.infra.PropertyManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BaseConfig.class)
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

    @Resource
    CsvFileReader target;

    @Resource
    PropertyManager pm;

    @Test
    public void test() {
        List<Map<String, String>> result = target.read("testdata/csv/CsvTestScriptBom.csv", false);
        assertThat(result.size(), is(17));
        assertThat(result.get(0), is(expectedHeader));
        assertThat(result.get(16), is(expectedFooter));
    }

    @Test
    public void testHeaderOnly() {
        List<Map<String, String>> result = target.read("testdata/csv/CsvTestScriptBom.csv", true);
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(expectedHeader));
    }

    @Test(expected = IllegalStateException.class)
    public void testException() {
        @SuppressWarnings("unused")
        List<Map<String, String>> result = target.read("illegalPath", false);
    }

    @Test
    public void testNoBom() {
        pm.setCsvCharset(StandardCharsets.UTF_8);
        pm.setCsvHasBOM(false);
        List<Map<String, String>> result = target.read("testdata/csv/CsvTestScriptNoBom.csv",
                false);
        assertThat(result.size(), is(17));
        assertThat(result.get(0), is(expectedHeader));
        assertThat(result.get(16), is(expectedFooter));
    }

    @Test
    public void testSJIS() {
        pm.setCsvCharset(Charset.forName("Windows-31j"));
        pm.setCsvHasBOM(false);
        List<Map<String, String>> result = target.read("testdata/csv/CsvTestScriptSJIS.csv", false);
        assertThat(result.size(), is(17));
        assertThat(result.get(0), is(expectedHeader));
        assertThat(result.get(16), is(expectedFooter));
    }

    @Before
    public void setup() {
        pm.setCsvCharset(StandardCharsets.UTF_8);
        pm.setCsvHasBOM(true);
    }

}
