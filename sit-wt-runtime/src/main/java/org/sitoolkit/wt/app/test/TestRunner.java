package org.sitoolkit.wt.app.test;

import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.app.config.RuntimeConfig;
import org.sitoolkit.wt.domain.evidence.EvidenceOpener;
import org.sitoolkit.wt.domain.tester.TestEventListener;
import org.sitoolkit.wt.domain.tester.TestResult;
import org.sitoolkit.wt.domain.tester.Tester;
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

        TestRunner runner = new TestRunner();
        runner.run(appCtx, args[0], "TestScript", "", true);

    }

    public List<TestResult> run(ConfigurableApplicationContext appCtx, String scriptPathes,
            String sheetName, String caseNo, boolean isEvidenceOpen) {

        Tester tester = ApplicationContextHelper.getBean(Tester.class);

        List<TestResult> results = new ArrayList<>();

        try {

            for (String scriptPath : scriptPathes.split(",")) {

                scriptPath = scriptPath.trim();

                tester.prepare(scriptPath, sheetName, caseNo);
                results.add(tester.operate(caseNo));

                TestEventListener listener = ApplicationContextHelper
                        .getBean(TestEventListener.class);
                listener.before();
                tester.tearDown();

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
}
