/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.domain.tester;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.math.NumberUtils;
import org.sitoolkit.wt.domain.debug.DebugSupport;
import org.sitoolkit.wt.domain.evidence.DialogScreenshotSupport;
import org.sitoolkit.wt.domain.evidence.Evidence;
import org.sitoolkit.wt.domain.evidence.EvidenceManager;
import org.sitoolkit.wt.domain.evidence.LogLevelVo;
import org.sitoolkit.wt.domain.evidence.LogRecord;
import org.sitoolkit.wt.domain.evidence.Screenshot;
import org.sitoolkit.wt.domain.evidence.ScreenshotTaker;
import org.sitoolkit.wt.domain.evidence.ScreenshotTiming;
import org.sitoolkit.wt.domain.operation.OperationResult;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptCatalog;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.TestException;
import org.sitoolkit.wt.infra.VerifyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、テストの実施者を表すエンティティです。
 *
 * @author yuichi.kuwahara
 */
public class Tester {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    TestContext current;
    @Resource
    DebugSupport debug;
    @Resource
    DialogScreenshotSupport dialog;

    @Resource
    EvidenceManager em;

    @Resource
    ScreenshotTaker screenshotTaker;

    @Resource
    TestScriptDao dao;

    @Resource
    TestScriptCatalog catalog;

    // /**
    // * スクリーンショット操作
    // */
    // private ScreenshotOperation screenshotOpe;
    /**
     * テストスクリプトがロード済の場合にtrue
     */
    private boolean scriptLoaded = false;
    private TestScript testScript;

    public void setUp(String caseNo) {
        current.reset();
        current.setCaseNo(caseNo);
        current.setScriptName(testScript.getName());

        // opelog.beginScript();
    }

    public void prepare(String scriptPath, String sheetName, String caseNo) {
        log.info("テストスクリプトをロードします。{}, {}", scriptPath, sheetName);
        testScript = catalog.get(scriptPath, sheetName);
        current.setTestScript(testScript);
        current.setScriptName(testScript.getName());
        current.setCaseNo(caseNo);
        current.reset();
        log.debug("prepare test context {}", current);
    }

    /**
     * テストスクリプトファイルを読み込み内部に保持します。
     *
     * @param testScriptPath
     *            テストスクリプトのパス
     */
    public void setUpClass(String testScriptPath, String sheetName) {
        if (!isScriptLoaded()) {
            log.info("テストスクリプトをロードします。{}, {}", testScriptPath, sheetName);
            testScript = catalog.get(testScriptPath, sheetName);
            current.setTestScript(testScript);

            if (debug.isDebug()) {
                log.info("最後のステップにブレークポイントを設定します。");
                TestStep lastStep = testScript.getLastStep();
                lastStep.setBreakPoint("y");
            }

        }
    }

    public void tearDown() {
        // NOP
    }

    /**
     * ケース番号のテストスクリプトに従い、テストを実施します。
     *
     * @param caseNo
     *            ケース番号
     * @return Verify操作がNGとなった数
     */
    public TestResult operate(String caseNo) {

        if (!testScript.containsCaseNo(caseNo)) {
            String msg = "指定されたケース番号[" + caseNo + "]は不正です。指定可能なケース番号："
                    + testScript.getCaseNoMap().keySet();
            throw new TestException(msg);
        }

        dialog.checkReserve(testScript.getTestStepList(), caseNo);
        log.info("ケース{}を実行します", caseNo);

        List<Exception> ngList = new ArrayList<Exception>();
        TestResult result = new TestResult();

        Evidence evidence = em.createEvidence(current.getScriptName(), caseNo);
        TestStep testStep = null;

        try {

            do {
                testStep = current.getTestStep();
                dialog.reserveWindowRect(testStep.getNo());

                try {

                    operateOneScript(testStep, current.getCaseNo(), evidence);

                } catch (VerifyException e) {
                    ngList.add(e);
                    result.add(e);
                    evidence.addLogRecord(LogRecord.create(log, LogLevelVo.ERROR, testStep,
                            "期待と異なる結果になりました {}", e.getLocalizedMessage()));
                    addScreenshot(evidence, ScreenshotTiming.ON_ERROR);

                    if (debug.isDebug()) {
                        debug.pause();
                    }

                } catch (Exception e) {

                    if (debug.isDebug()) {
                        ngList.add(e);
                        evidence.addLogRecord(LogRecord.create(log, LogLevelVo.ERROR, testStep,
                                "予期しないエラーが発生しました {}", e.getLocalizedMessage()));
                        addScreenshot(evidence, ScreenshotTiming.ON_ERROR);
                        debug.pause();
                    } else {
                        throw e;
                    }

                }

            } while (debug.next());

        } catch (Exception e) {
            evidence.addLogRecord(LogRecord.create(log, LogLevelVo.ERROR, testStep,
                    "予期しないエラーが発生しました {}", e.getLocalizedMessage()));
            addScreenshot(evidence, ScreenshotTiming.ON_ERROR);
            log.debug("例外詳細", e);
            result.setErrorCause(e);
        } finally {
            em.flushEvidence(evidence);
        }

        return result;
    }

    /**
     * 1件のテストステップを実施します。 テストステップに指示がある場合、 実施前または後でスクリーンショットを取得します。
     *
     * @param testStep
     *            テストステップ
     * @param caseNo
     *            ケース番号
     * @see TestStep#execute(int)
     * @see TestStep#beforeScreenshot()
     * @see TestStep#afterScreenshot()
     */
    private void operateOneScript(TestStep testStep, String caseNo, Evidence evidence) {
        testStep.setCurrentCaseNo(caseNo);

        if (testStep.isSkip()) {
            log.info("ケース[{}][{} {}]の操作をスキップします", caseNo, testStep.getNo(), testStep.getItemName());
            return;
        }

        if (testStep.dialogScreenshot()) {
            addScreenshot(evidence, ScreenshotTiming.ON_DIALOG);
        } else if (testStep.beforeScreenshot()) {
            addScreenshot(evidence, ScreenshotTiming.BEFORE_OPERATION);
        }

        OperationResult result = testStep.getOperation().operate(testStep);
        evidence.addLogRecords(result.getRecords());

        if (testStep.afterScreenshot()) {
            addScreenshot(evidence, ScreenshotTiming.AFTER_OPERATION);
        }

        evidence.commitScreenshot();

        try {
            Thread.sleep(getOperationSpan());
        } catch (InterruptedException e) {
            log.warn("スレッドの待機に失敗しました", e);
        }
    }

    private void addScreenshot(Evidence evidence, ScreenshotTiming timing) {
        Screenshot screenshot = screenshotTaker.get(timing);
        evidence.addScreenshot(screenshot, current.getScreenshotTiming());
        em.moveScreenshot(evidence, current.getTestStepNo(), current.getItemName());
    }

    /**
     * 操作実行後に待機する時間間隔を取得します。
     * 
     * @return 操作実行後に待機する時間間隔
     */
    public int getOperationSpan() {
        return NumberUtils.toInt(System.getProperty("operationSpan"), 0);
    }

    public boolean isScriptLoaded() {
        return scriptLoaded;
    }

}
