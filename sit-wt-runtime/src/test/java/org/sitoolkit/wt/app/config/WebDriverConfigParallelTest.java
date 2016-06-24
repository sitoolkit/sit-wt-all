package org.sitoolkit.wt.app.config;

import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;

public class WebDriverConfigParallelTest extends SitTesterTestBase {

    @Test
    public void test001() {
        test();
    }

    @Test
    public void test002() {
        test();
    }

    @Test
    public void test003() {
        test();
    }

    @Test
    public void test004() {
        test();
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/WebDriverConfigParallelTestScript.xlsx";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
