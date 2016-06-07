package org.sitoolkit.tester.appium;

import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "android", "androidNativeDemo", "debug" })
public class AndroidNativeTest extends SitTesterTestBase {

    @Test
    public void test001() {
        test();
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/appium/AndroidNativeTest.xlsx";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
