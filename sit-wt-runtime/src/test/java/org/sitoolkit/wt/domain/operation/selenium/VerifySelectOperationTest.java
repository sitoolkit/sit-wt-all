package org.sitoolkit.wt.domain.operation.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;
import org.sitoolkit.wt.domain.tester.TestResult;

public class VerifySelectOperationTest extends SitTesterTestBase {

    @Test
    public void test001() {
        test();
    }

    @Test
    public void test002() {
        System.err.println(System.getProperties());
        TestResult result = tester.operate(getCurrentCaseNo());

        assertThat(result.buildReason(), result.getFailCount(), is(8));
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/selenium/VerifySelectOperationTestScript.csv";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
