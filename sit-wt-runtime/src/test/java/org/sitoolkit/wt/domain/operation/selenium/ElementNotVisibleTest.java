package org.sitoolkit.wt.domain.operation.selenium;

import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;

public class ElementNotVisibleTest extends SitTesterTestBase {

    @Test
    public void test001() {
        test();
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/selenium/ElementNotVisibleTestScript.xlsx";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
