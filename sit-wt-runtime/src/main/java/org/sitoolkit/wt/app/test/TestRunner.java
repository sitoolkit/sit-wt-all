package org.sitoolkit.wt.app.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.app.config.RuntimeConfig;
import org.sitoolkit.wt.domain.evidence.EvidenceOpener;
import org.sitoolkit.wt.domain.tester.TestEventListener;
import org.sitoolkit.wt.domain.tester.TestResult;
import org.sitoolkit.wt.domain.tester.Tester;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptCatalog;
import org.sitoolkit.wt.infra.ApplicationContextHelper;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestRunner {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("テストスクリプトを指定してください。");
            System.exit(0);
        }

        ConfigurableApplicationContext appCtx = new AnnotationConfigApplicationContext(
                RuntimeConfig.class);

        String caseNo = args.length > 2 ? args[1] : "";

        TestRunner runner = new TestRunner();
        runner.run(appCtx, args[0], "TestScript", caseNo, true);

    }

    public List<TestResult> run(ConfigurableApplicationContext appCtx, String scriptPath,
            String sheetName, String caseNo, boolean isEvidenceOpen) {

        List<TestResult> results = new ArrayList<>();

        try {

            if (StringUtils.isEmpty(caseNo)) {

                TestScriptCatalog catalog = ApplicationContextHelper
                        .getBean(TestScriptCatalog.class);
                TestScript script = catalog.get(scriptPath, sheetName);

                for (String caseNoInScript : script.getCaseNoMap().keySet()) {
                    results.add(run(scriptPath, sheetName, caseNoInScript));
                }

            } else {

                results.add(run(scriptPath, sheetName, caseNo));

            }

        } finally {

            appCtx.close();

        }

        if (isEvidenceOpen) {
            EvidenceOpener opener = new EvidenceOpener();
            opener.open();
        }

        return results;
    }

    private TestResult run(String scriptPath, String sheetName, String caseNo) {
        Tester tester = ApplicationContextHelper.getBean(Tester.class);
        TestEventListener listener = ApplicationContextHelper.getBean(TestEventListener.class);

        tester.prepare(scriptPath, sheetName, caseNo);
        listener.before();

        TestResult result = tester.operate(caseNo);

        listener.after();
        tester.tearDown();

        return result;
    }
}
