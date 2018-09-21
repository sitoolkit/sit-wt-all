package io.sitoolkit.tester.appium;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import io.sitoolkit.wt.domain.tester.SitTesterTestBase;

@ActiveProfiles({ "android", "androidNativeDemo", "debug" })
public class AndroidNativeTest extends SitTesterTestBase {

    @Test
    public void test001() {
        test();
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/appium/AndroidNativeTest.csv";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
