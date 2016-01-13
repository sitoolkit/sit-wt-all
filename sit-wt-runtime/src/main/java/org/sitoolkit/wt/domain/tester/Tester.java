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
import org.sitoolkit.wt.domain.evidence.OperationLog;
import org.sitoolkit.wt.domain.operation.ScreenshotOperation;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.TestException;
import org.sitoolkit.wt.infra.VerifyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * このクラスは、テストの実施者を表すエンティティです。
 *
 * @author yuichi.kuwahara
 */
public class Tester {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Resource
    ApplicationContext appCtx;
    @Resource
    OperationLog opelog;
    @Resource
    TestContext current;
    @Resource
    DebugSupport debug;
    @Resource
    DialogScreenshotSupport dialog;

    @Resource
    TestScriptDao dao;

    /**
     * スクリーンショット操作
     */
    private ScreenshotOperation screenshotOpe;
    /**
     * テストスクリプトがロード済の場合にtrue
     */
    private boolean scriptLoaded = false;
    private TestScript testScript;

    public void setUp() {
        // NOP
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
            testScript = dao.load(testScriptPath, sheetName, false);
            current.setTestScript(testScript);

            if (debug.isDebug()) {
                log.info("最後のステップにブレークポイントを設定します。");
                TestStep lastStep = testScript.getLastStep();
                lastStep.setBreakPoint("y");
            }

        }
    }

    /**
     * 現在実行中のケース番号の操作ログを出力します。
     *
     */
    public void tearDown() {
        opelog.flush();
    }

    public void tearDownClass() {
        opelog.moveLogFile();
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
        current.reset();
        current.setCaseNo(caseNo);
        current.setScriptName(testScript.getName());
        dialog.checkReserve(testScript.getTestStepList(), caseNo);
        log.info("ケース{}を実行します", caseNo);

        List<Exception> ngList = new ArrayList<Exception>();
        TestResult result = new TestResult();

        try {
            do {
                TestStep testStep = current.getTestStep();
                dialog.reserveWindowRect(testStep.getNo());
                try {
                    operateOneScript(testStep, current.getCaseNo());
                } catch (VerifyException e) {
                    ngList.add(e);
                    result.add(e);
                    opelog.warn(log, "期待と異なる結果になりました。{}", e.getLocalizedMessage());
                    opelog.addScreenshot(screenshotOpe.get());
                    if (debug.isDebug()) {
                        debug.pause();
                    }
                } catch (Exception e) {
                    if (debug.isDebug()) {
                        ngList.add(e);
                        log.error("予期しないエラーが発生しました。{}", e.getLocalizedMessage());
                        opelog.addScreenshot(screenshotOpe.get());
                        debug.pause();
                    } else {
                        throw e;
                    }
                }
            } while (debug.next());

        } catch (Exception e) {
            opelog.addScreenshot(screenshotOpe.get(), "テスト実施が異常終了");
            opelog.error(log, e.getMessage());
            log.debug("例外詳細", e);
            result.setErrorCause(e);
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
    void operateOneScript(TestStep testStep, String caseNo) {
        testStep.setCurrentCaseNo(caseNo);
        if (testStep.isSkip()) {
            log.info("ケース[{}][{} {}]の操作をスキップします", caseNo, testStep.getNo(), testStep.getItemName());
            return;
        }

        if (testStep.dialogScreenshot()) {
            opelog.addScreenshot(screenshotOpe.getWithDialog());
        } else if (testStep.beforeScreenshot()) {
            opelog.addScreenshot(screenshotOpe.get(), "前");
        }

        testStep.execute();

        if (testStep.afterScreenshot()) {
            opelog.addScreenshot(screenshotOpe.get(), "後");
        }

        opelog.flushOneStep();

        try {
            Thread.sleep(getOperationSpan());
        } catch (InterruptedException e) {
            log.warn("スレッドの待機に失敗しました", e);
        }
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

    public ScreenshotOperation getScreenshotOpe() {
        return screenshotOpe;
    }

    public void setScreenshotOpe(ScreenshotOperation screenshotOpe) {
        this.screenshotOpe = screenshotOpe;
    }
}
