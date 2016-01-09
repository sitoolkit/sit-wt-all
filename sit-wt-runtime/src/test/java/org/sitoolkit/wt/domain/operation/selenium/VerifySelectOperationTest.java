package org.sitoolkit.wt.domain.operation.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;
import org.sitoolkit.wt.domain.tester.TestResult;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        VerifySelectOperationTest.class})
public class VerifySelectOperationTest extends SitTesterTestBase {

    @Test
    public void test001() {
        test();
    }

    @Test
    public void test002() {
        System.err.println(System.getProperties());
        TestResult result = tester.operate(getCurrentCaseNo());

        assertThat(result.buileReason(), result.getFailCount(), is(8));
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/selenium/VerifySelectOperationTestScript.xlsx";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
