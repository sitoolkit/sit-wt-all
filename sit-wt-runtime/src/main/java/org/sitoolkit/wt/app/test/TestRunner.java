package org.sitoolkit.wt.app.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.app.config.RuntimeConfig;
import org.sitoolkit.wt.app.selenium2script.Selenium2Script;
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
            System.exit(1);
        }

        String caseNo = args.length > 2 ? args[1] : "";
        boolean isParallel = Boolean.getBoolean("sitwt.parallel");
        boolean isEvidenceOpen = Boolean.getBoolean("sitwt.open-evidence");

        TestRunner runner = new TestRunner();
        runner.runScript(args[0], "TestScript", caseNo, isParallel, isEvidenceOpen);

    }

    /**
     * テストスクリプトを実行します。
     *
     * @param scriptPath
     *            実行対象のテストスクリプト
     * @param sheetName
     *            テストスクリプト内で実行対象のシート名
     * @param caseNo
     *            実行対象のケース番号 指定しない場合はシート内の全ケースを実行します。
     * @param isParallel
     *            ケースを並列に実行する場合にtrue
     * @param isEvidenceOpen
     *            テスト実行後にエビデンスを開く場合にtrue
     * @return テスト結果
     */
    public List<TestResult> runScript(String scriptPath, String sheetName, String caseNo,
            boolean isParallel, boolean isEvidenceOpen) {

        ConfigurableApplicationContext appCtx = new AnnotationConfigApplicationContext(
                RuntimeConfig.class);

        List<TestResult> result = runScript(appCtx, scriptPath, sheetName, caseNo, isParallel,
                isEvidenceOpen);

        appCtx.close();

        return result;
    }

    /**
     * テストスクリプトを実行します。
     *
     * @param appCtx
     *            {@link RuntimeConfig}で構成された
     *            {@code ConfigurableApplicationContext}
     * @param scriptPath
     *            実行対象のテストスクリプト
     * @param sheetName
     *            テストスクリプト内で実行対象のシート名
     * @param caseNo
     *            実行対象のケース番号 指定しない場合はシート内の全ケースを実行します。
     * @param isParallel
     *            ケースを並列に実行する場合にtrue
     * @param isEvidenceOpen
     *            テスト実行後にエビデンスを開く場合にtrue
     * @return テスト結果
     */
    public List<TestResult> runScript(ConfigurableApplicationContext appCtx, String scriptPath,
            String sheetName, String caseNo, boolean isParallel, boolean isEvidenceOpen) {

        LOG.info("テストスクリプトを実行します。{} {} {}", scriptPath, sheetName, caseNo);

        if (scriptPath.endsWith(".html")) {
            scriptPath = selenium2script(scriptPath).getAbsolutePath();
        }

        List<TestResult> results = new ArrayList<>();

        if (StringUtils.isEmpty(caseNo)) {

            if (isParallel) {
                results.addAll(runAllCasesInParallel(scriptPath, sheetName));
            } else {
                results.addAll(runAllCase(scriptPath, sheetName));
            }

        } else {

            results.add(runCase(scriptPath, sheetName, caseNo));

        }

        if (isEvidenceOpen) {
            EvidenceOpener opener = new EvidenceOpener();
            opener.open();
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

    private List<TestResult> runAllCase(String scriptPath, String sheetName) {

        List<TestResult> results = new ArrayList<>();
        TestScriptCatalog catalog = ApplicationContextHelper.getBean(TestScriptCatalog.class);
        TestScript script = catalog.get(scriptPath, sheetName);

        for (String caseNoInScript : script.getCaseNoMap().keySet()) {
            results.add(runCase(scriptPath, sheetName, caseNoInScript));
        }

        return results;
    }

    private List<TestResult> runAllCasesInParallel(String scriptPath, String sheetName) {

        List<TestResult> results = new ArrayList<>();
        TestScriptCatalog catalog = ApplicationContextHelper.getBean(TestScriptCatalog.class);
        TestScript script = catalog.get(scriptPath, sheetName);

        // run last case in current thread to use WebDriver instance bound in
        // current thread
        List<String> caseNoList = new ArrayList<>(script.getCaseNoMap().keySet());

        if (caseNoList.isEmpty()) {
            LOG.warn("テストスクリプトにケースがありません　{} {}", scriptPath, sheetName);
            return results;
        }

        String lastCaseNo = caseNoList.get(caseNoList.size() - 1);
        caseNoList.remove(caseNoList.size() - 1);

        ExecutorService executor = Executors.newCachedThreadPool();

        for (String caseNoInScript : caseNoList) {

            executor.execute(() -> {
                results.add(runCase(scriptPath, sheetName, caseNoInScript));
            });

        }

        results.add(runCase(scriptPath, sheetName, lastCaseNo));

        executor.shutdown();

        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            LOG.warn("スレッドの待機で例外が発生しました", e);
        }

        return results;
    }

    private TestResult runCase(String scriptPath, String sheetName, String caseNo) {
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
