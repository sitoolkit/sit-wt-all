package org.sitoolkit.wt.app.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class TestCaseReaderTest {

    TestCaseReader reader = new TestCaseReader();

    @Test
    public void test() {
        List<String> caseNos = reader.read("testscript/CsvTestScript.csv", "TestScript");

        assertThat(caseNos.size(), is(2));
        assertThat("001", is(caseNos.get(0)));
        assertThat("002", is(caseNos.get(1)));
    }

}
