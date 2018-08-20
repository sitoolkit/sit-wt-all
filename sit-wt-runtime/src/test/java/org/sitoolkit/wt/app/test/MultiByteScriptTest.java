package org.sitoolkit.wt.app.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;
import org.springframework.context.ConfigurableApplicationContext;

public class MultiByteScriptTest extends SitTesterTestBase {

    @Resource
    ConfigurableApplicationContext appCtx;

    @Test
    public void testMultiByteCase() {

        boolean isParallel = Boolean.getBoolean("sitwt.parallel");
        boolean isEvidenceOpen = Boolean.getBoolean("sitwt.open-evidence");
        TestRunner testRunner = new TestRunner();

        testRunner.runScript(appCtx, getTestScriptPath(), isParallel, isEvidenceOpen);
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/テスト-スクリプト(サンプル).csv";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }
}
