package org.sitoolkit.wt.app.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.app.config.RuntimeConfig;
import org.sitoolkit.wt.domain.evidence.EvidenceOpener;
import org.sitoolkit.wt.domain.tester.TestEventListener;
import org.sitoolkit.wt.domain.tester.TestResult;
import org.sitoolkit.wt.domain.tester.Tester;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptCatalog;
import org.sitoolkit.wt.infra.ApplicationContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunner.class);

    public static void main(String[] args) {

        if (args.length < 1) {
            LOG.info("テストスクリプトを指定してください。");
            LOG.info(">java {} [path/to/TestScript.xlsx]", TestRunner.class.getName());
            System.exit(0);
        }

        String caseNo = args.length > 2 ? args[1] : "";

        TestRunner runner = new TestRunner();
        runner.run(args[0], "TestScript", caseNo, true);

    }

    public List<TestResult> run(String scriptPath, String sheetName, String caseNo,
            boolean isEvidenceOpen) {

        ConfigurableApplicationContext appCtx = new AnnotationConfigApplicationContext(
                RuntimeConfig.class);

        List<TestResult> result = run(appCtx, scriptPath, sheetName, caseNo, isEvidenceOpen);

        appCtx.close();

        return result;
    }

    /**
     * テストスクリプトを実行します。
     *
     * @param appCtx
     *            {@link RuntimeConfig}で構成された{@code ConfigurableApplicationContext}
     * @param scriptPath
     *            実行対象のテストスクリプト
     * @param sheetName
     *            テストスクリプト内で実行対象のシート名
     * @param caseNo
     *            実行対象のケース番号 指定しない場合はシート内の全ケースを実行します。
     * @param isEvidenceOpen
     *            テスト実行後にエビデンスを開く場合にtrue
     * @return
     */
    public List<TestResult> run(ConfigurableApplicationContext appCtx, String scriptPath,
            String sheetName, String caseNo, boolean isEvidenceOpen) {

        List<TestResult> results = new ArrayList<>();

        if (StringUtils.isEmpty(caseNo)) {

            results.addAll(runAll(scriptPath, sheetName));

        } else {

            results.add(run(scriptPath, sheetName, caseNo));

        }

        if (isEvidenceOpen) {
            EvidenceOpener opener = new EvidenceOpener();
            opener.open();
        }

        return results;
    }

    private List<TestResult> runAll(String scriptPath, String sheetName) {

        List<TestResult> results = new ArrayList<>();
        TestScriptCatalog catalog = ApplicationContextHelper.getBean(TestScriptCatalog.class);
        TestScript script = catalog.get(scriptPath, sheetName);

        ExecutorService executor = Executors.newCachedThreadPool();

        for (String caseNoInScript : script.getCaseNoMap().keySet()) {

            executor.execute(() -> {
                results.add(run(scriptPath, sheetName, caseNoInScript));
            });

        }

        executor.shutdown();

        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            LOG.warn("スレッドの待機で例外が発生しました", e);
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

        // TODO 例外処理

        return result;
    }
}
