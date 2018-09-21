package io.sitoolkit.tester.appium;

import org.junit.Test;

import io.sitoolkit.wt.domain.tester.SitTesterTestBase;

public class AndroidHybridTest extends SitTesterTestBase {

    @Test
    public void test001() {
        test();
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/appium/HybridTest.csv";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
