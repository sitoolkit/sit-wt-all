package org.sitoolkit.tester.appium;

import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;

public class AndroidHybridTest extends SitTesterTestBase {

    @Test
    public void test001() {
        test();
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/appium/HybridTest.xlsx";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
