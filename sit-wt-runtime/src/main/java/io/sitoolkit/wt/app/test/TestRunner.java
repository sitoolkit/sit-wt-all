package io.sitoolkit.wt.app.test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import io.sitoolkit.wt.app.compareevidence.DiffEvidenceGenerator;
import io.sitoolkit.wt.app.compareevidence.DiffEvidenceGeneratorConfig;
import io.sitoolkit.wt.app.config.RuntimeConfig;
import io.sitoolkit.wt.app.selenium2script.Selenium2Script;
import io.sitoolkit.wt.domain.evidence.EvidenceDir;
import io.sitoolkit.wt.domain.evidence.EvidenceOpener;
import io.sitoolkit.wt.domain.tester.TestEventListener;
import io.sitoolkit.wt.domain.tester.TestResult;
import io.sitoolkit.wt.domain.tester.Tester;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestScriptCatalog;
import io.sitoolkit.wt.infra.ApplicationContextHelper;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.resource.MessageManager;

public class TestRunner {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(TestRunner.class);

  public static void main(String[] args) {

    if (args.length < 1) {
      LOG.info("test.script.appoint");
      LOG.info("test.case", TestRunner.class.getName());
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

      ApplicationContext appCtx =
          new AnnotationConfigApplicationContext(DiffEvidenceGeneratorConfig.class);
      DiffEvidenceGenerator generator = appCtx.getBean(DiffEvidenceGenerator.class);

      EvidenceDir targetDir = EvidenceDir.getLatest();
      EvidenceDir baseDir = EvidenceDir.baseEvidenceDir(null, targetDir.getBrowser());

      boolean compareSsSuccess = generator.generate(baseDir, targetDir, isCompareScreenshot);
      LOG.info("base.evidence.compare", compareSsSuccess ? MessageManager.getMessage("success")
          : MessageManager.getMessage("failure"));

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
   * @param testCaseStr 実行するテストケース(scriptPath1,scriptPath2#case_1,scriptPath3!TestScript#case_1)
   * @param isParallel ケースを並列に実行する場合にtrue
   * @param isEvidenceOpen テスト実行後にエビデンスを開く場合にtrue
   * @return テスト結果
   */
  public List<TestResult> runScript(String testCaseStr, boolean isParallel,
      boolean isEvidenceOpen) {

    ConfigurableApplicationContext appCtx =
        new AnnotationConfigApplicationContext(RuntimeConfig.class);

    List<TestResult> result = runScript(appCtx, testCaseStr, isParallel, isEvidenceOpen);

    appCtx.close();

    return result;

  }

  /**
   * テストスクリプトを実行します。
   *
   * @param appCtx {@link RuntimeConfig}で構成された {@code ConfigurableApplicationContext}
   * @param testCaseStr 実行するテストケース(scriptPath1,scriptPath2#case_1,scriptPath3!TestScript#case_1)
   * @param isParallel ケースを並列に実行する場合にtrue
   * @param isEvidenceOpen テスト実行後にエビデンスを開く場合にtrue
   * @return テスト結果
   */
  public List<TestResult> runScript(ConfigurableApplicationContext appCtx, String testCaseStr,
      boolean isParallel, boolean isEvidenceOpen) {

    List<TestResult> results = new ArrayList<>();
    List<TestCase> allTestCase = new ArrayList<>();
    for (String testCondition : testCaseStr.split(",")) {
      TestCase testCase = TestCase.parse(testCondition);

      if (!testCase.getScriptPath().endsWith(Selenium2Script.SCRIPT_EXTENSION)) {
        allTestCase.add(testCase);
        continue;
      }

      selenium2scripts(testCase.getScriptPath()).forEach(testScript -> {
        TestCase seleniumTestCase = new TestCase();
        seleniumTestCase.setScriptPath(testScript.toAbsolutePath().toString());
        seleniumTestCase.setCaseNo(Selenium2Script.DEFAULT_CASE_NO);
        allTestCase.add(seleniumTestCase);
      });
    }

    if (isParallel) {
      results.addAll(runAllCasesInParallel(allTestCase, isEvidenceOpen));
    } else {
      results.addAll(runAllCase(allTestCase, isEvidenceOpen));
    }

    return results;
  }

  private List<Path> selenium2scripts(String seleniumScriptPath) {
    Selenium2Script s2s = Selenium2Script.initInstance();
    s2s.setOpenScript(false);
    s2s.setOverwriteScript(false);

    Path seleniumScript = Paths.get(seleniumScriptPath);

    List<Path> scripts = s2s.convert(seleniumScript);
    s2s.backup(seleniumScript);

    return scripts;
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

  private List<TestResult> runAllCasesInParallel(List<TestCase> testCases, boolean isEvidenceOpen) {

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
          LOG.warn("case.empty", scriptPath, sheetName);
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
      LOG.warn("thread.sleep.exception", e);
    }

    return results;
  }

  private TestResult runCase(String scriptPath, String sheetName, String caseNo) {
    LOG.info("run.case", scriptPath, sheetName, caseNo);

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
        LOG.info("test.result", caseNo, result.isSuccess() ? MessageManager.getMessage("success")
            : MessageManager.getMessage("failure"));
      }
    }

    return result;
  }
}
