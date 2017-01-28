package org.sitoolkit.wt.app.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.app.compareevidence.DiffEvidenceGenerator;
import org.sitoolkit.wt.app.compareevidence.DiffEvidenceGeneratorConfig;
import org.sitoolkit.wt.app.config.RuntimeConfig;
import org.sitoolkit.wt.app.selenium2script.Selenium2Script;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.domain.evidence.EvidenceOpener;
import org.sitoolkit.wt.domain.tester.TestEventListener;
import org.sitoolkit.wt.domain.tester.TestResult;
import org.sitoolkit.wt.domain.tester.Tester;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptCatalog;
import org.sitoolkit.wt.infra.ApplicationContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunner.class);

    public static void main(String[] args) {

        if (args.length < 1) {
            LOG.info("テストスクリプトを指定してください。");
            LOG.info(">java {} [path/to/TestScript.xlsx!TestSheet#CaseNo]",
                    TestRunner.class.getName());
            System.exit(1);
        }

        boolean isParallel = Boolean.getBoolean("sitwt.parallel");
        boolean isEvidenceOpen = Boolean.getBoolean("sitwt.open-evidence");
        boolean isCompareScreenshot = Boolean.getBoolean("sitwt.compare-screenshot");

        TestRunner runner = new TestRunner();
        List<TestResult> results = runner.runScript(args[0], isParallel, isEvidenceOpen);

        for (TestResult resuls : results) {
            if (!resuls.isSuccess()) {
                System.exit(2);
            }
        }

        if (isCompareScreenshot) {

            ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                    DiffEvidenceGeneratorConfig.class);
            DiffEvidenceGenerator generator = appCtx.getBean(DiffEvidenceGenerator.class);

            EvidenceDir targetDir = EvidenceDir.getLatest();
            EvidenceDir baseDir = EvidenceDir.baseEvidenceDir(null, targetDir.getBrowser());

            boolean compareSsSuccess = generator.generate(baseDir, targetDir, isCompareScreenshot);
            LOG.info("基準エビデンスとのスクリーンショット比較が{}しました。", compareSsSuccess ? "成功" : "失敗");

            if (!compareSsSuccess) {
                EvidenceOpener opener = new EvidenceOpener();
                opener.openCompareNgEvidence(targetDir);
            }

        }

        System.exit(0);
    }

    /**
     * テストスクリプトを実行します。
     *
     * @param testCaseStr
     *            実行するテストケース(scriptPath1,scriptPath2#case_1,scriptPath3!TestScript#case_1)
     * @param isParallel
     *            ケースを並列に実行する場合にtrue
     * @param isEvidenceOpen
     *            テスト実行後にエビデンスを開く場合にtrue
     * @return テスト結果
     */
    public List<TestResult> runScript(String testCaseStr, boolean isParallel,
            boolean isEvidenceOpen) {

        ConfigurableApplicationContext appCtx = new AnnotationConfigApplicationContext(
                RuntimeConfig.class);

        List<TestResult> result = runScript(appCtx, testCaseStr, isParallel, isEvidenceOpen);

        appCtx.close();

        return result;

    }

    /**
     * テストスクリプトを実行します。
     *
     * @param appCtx
     *            {@link RuntimeConfig}で構成された
     *            {@code ConfigurableApplicationContext}
     * @param testCaseStr
     *            実行するテストケース(scriptPath1,scriptPath2#case_1,scriptPath3!TestScript#case_1)
     * @param isParallel
     *            ケースを並列に実行する場合にtrue
     * @param isEvidenceOpen
     *            テスト実行後にエビデンスを開く場合にtrue
     * @return テスト結果
     */
    public List<TestResult> runScript(ConfigurableApplicationContext appCtx, String testCaseStr,
            boolean isParallel, boolean isEvidenceOpen) {

        List<TestResult> results = new ArrayList<>();
        List<TestCase> allTestCase = new ArrayList<>();
        for (String testCondition : testCaseStr.split(",")) {
            TestCase testCase = TestCase.parse(testCondition);

            if (testCase.getScriptPath().endsWith(".html")) {
                testCase.setScriptPath(selenium2script(testCase.getScriptPath()).getAbsolutePath());
            }

            allTestCase.add(testCase);
        }

        if (isParallel) {
            results.addAll(runAllCasesInParallel(allTestCase, isEvidenceOpen));
        } else {
            results.addAll(runAllCase(allTestCase, isEvidenceOpen));
        }

        return results;
    }

    private File selenium2script(String seleniumScriptPath) {
        Selenium2Script s2s = Selenium2Script.initInstance();
        s2s.setOpenScript(false);
        s2s.setOverwriteScript(false);

        File seleniumScript = new File(seleniumScriptPath);

        File script = s2s.convert(seleniumScript);
        s2s.backup(seleniumScript);

        return script;
    }

    private List<TestResult> runAllCase(List<TestCase> testCases, boolean isEvidenceOpen) {

        List<TestResult> results = new ArrayList<>();
        TestScriptCatalog catalog = ApplicationContextHelper.getBean(TestScriptCatalog.class);

        for (TestCase testCase : testCases) {
            String scriptPath = testCase.getScriptPath();
            String sheetName = testCase.getSheetName();
            String caseNo = testCase.getCaseNo();
            TestScript script = catalog.get(scriptPath, sheetName);

            if (StringUtils.isEmpty(caseNo)) {
                for (String caseNoInScript : script.getCaseNoMap().keySet()) {
                    results.add(runCase(scriptPath, sheetName, caseNoInScript));
                }

            } else {
                results.add(runCase(scriptPath, sheetName, caseNo));

            }

            if (isEvidenceOpen) {
                EvidenceOpener opener = new EvidenceOpener();
                opener.openTarget(new File(scriptPath));
            }
        }

        return results;
    }

    private List<TestResult> runAllCasesInParallel(List<TestCase> testCases,
            boolean isEvidenceOpen) {

        List<TestResult> results = new ArrayList<>();
        TestScriptCatalog catalog = ApplicationContextHelper.getBean(TestScriptCatalog.class);
        ExecutorService executor = Executors.newCachedThreadPool();

        for (TestCase testCase : testCases) {
            String scriptPath = testCase.getScriptPath();
            String sheetName = testCase.getSheetName();
            String caseNo = testCase.getCaseNo();
            TestScript script = catalog.get(scriptPath, sheetName);

            if (StringUtils.isEmpty(caseNo)) {
                // run last case in current thread to use WebDriver instance
                // bound
                // in current thread
                List<String> caseNoList = new ArrayList<>(script.getCaseNoMap().keySet());

                if (caseNoList.isEmpty()) {
                    LOG.warn("テストスクリプトにケースがありません　{} {}", scriptPath, sheetName);
                    continue;
                }

                String lastCaseNo = caseNoList.get(caseNoList.size() - 1);
                caseNoList.remove(caseNoList.size() - 1);

                for (String caseNoInScript : caseNoList) {

                    executor.execute(() -> {
                        results.add(runCase(scriptPath, sheetName, caseNoInScript));
                    });

                }

                results.add(runCase(scriptPath, sheetName, lastCaseNo));

            } else {
                executor.execute(() -> {
                    results.add(runCase(scriptPath, sheetName, caseNo));
                });
            }

            if (isEvidenceOpen) {
                EvidenceOpener opener = new EvidenceOpener();
                opener.openTarget(new File(scriptPath));
            }
        }

        executor.shutdown();

        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            LOG.warn("スレッドの待機で例外が発生しました", e);
        }

        return results;
    }

    private TestResult runCase(String scriptPath, String sheetName, String caseNo) {
        LOG.info("テストスクリプトを実行します。{} {} {}", scriptPath, sheetName, caseNo);

        Tester tester = ApplicationContextHelper.getBean(Tester.class);
        TestEventListener listener = ApplicationContextHelper.getBean(TestEventListener.class);

        tester.prepare(scriptPath, sheetName, caseNo);
        listener.before();

        TestResult result = null;

        try {
            result = tester.operate(caseNo);
        } finally {
            listener.after();
            tester.tearDown();

            if (result != null) {
                LOG.info("ケース{}が{}しました", caseNo, result.isSuccess() ? "成功" : "失敗");
            }
        }

        return result;
    }
}
