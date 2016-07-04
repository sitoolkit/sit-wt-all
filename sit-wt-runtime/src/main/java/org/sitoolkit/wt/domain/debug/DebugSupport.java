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
package org.sitoolkit.wt.domain.debug;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author yuichi.kuwahara
 */
public class DebugSupport {

    private static Logger LOG = LoggerFactory.getLogger(DebugSupport.class);

    private static final String USAGE_DESC = CommandKey.buildUsage();

    @Resource
    TestContext current;

    @Resource
    TestScriptDao dao;

    @Resource
    ApplicationContext appCtx;

    /**
     * 一時停止する間隔
     */
    private int pauseSpan = 800;

    private ExecutorService executor;

    private DebugCommand cmd;

    private boolean paused = false;

    private Boolean debug;

    /**
     * テスト実行を継続する場合にtrueを返します。
     *
     * @return テスト実行を継続する場合にtrue
     */
    public boolean next() {
        final int currentIndex = current.getCurrentIndex();
        int nextIndex = currentIndex + 1;
        TestScript testScript = current.getTestScript();
        LOG.debug("debugging test context : {}", current);
        if (currentIndex < testScript.getTestStepCount()) {

            if (isDebug()) {

                LOG.debug("currentIndex:{}, testStepCount:{}", currentIndex,
                        testScript.getTestStepCount());

                TestStep currentStep = testScript.getTestStep(currentIndex);

                if (currentStep != null && StringUtils.isNotEmpty(currentStep.getBreakPoint())) {
                    LOG.info("ブレークポイントが設定されています。");
                    pause();
                }
                nextIndex = getNextIndex(currentIndex);
            }

            if (nextIndex >= testScript.getTestStepCount()) {
                return current.isContinued();
            }

            current.setCurrentIndex(nextIndex);
            current.setTestStep(current.getTestScript().getTestStep(nextIndex));

            return true;
        } else {
            return current.isContinued();
        }
    }

    public synchronized boolean isDebug() {
        if (debug == null) {
            debug = getSysPropAsBoolean("sitwt.debug");
        }
        return debug;
    }

    private boolean getSysPropAsBoolean(String key) {
        return Boolean.parseBoolean(System.getProperty(key));
    }

    /**
     *
     * @param currentIndex
     *            現在のテストステップインデックス
     * @return 次に実行するテストステップインデックス
     */
    protected int getNextIndex(final int currentIndex) {

        if (!isPaused()) {
            return currentIndex + 1;
        }

        int ret = currentIndex;

        while (isPaused()) {
            try {
                Thread.sleep(getPauseSpan());
            } catch (InterruptedException e) {
                LOG.warn("スレッドの待機に失敗しました", e);
            }

            TestScript testScript = current.getTestScript();
            if (testScript.isScriptFileChanged()) {
                LOG.info("テストスクリプトが変更されています。再読込します。");
                current.setTestScript(
                        dao.load(testScript.getScriptFile(), testScript.getSheetName(), false));
            }

            // コンソールから有効なコマンド入力があるまでループします。
            if (cmd == null || cmd.key == null) {
                continue;
            }

            final int cmdRet = cmd.execute(ret, current.getTestScript(), appCtx);

            if (cmdRet < 0) {
                LOG.info("不正な操作です。");
            } else {
                ret = cmdRet;

                TestStep nextStep = current.getTestScript().getTestStep(ret);
                if (nextStep == null) {
                    LOG.info("全てのテストステップが終了しました。");
                } else {
                    LOG.info("現在のテストステップは{} {}({})です。", new Object[] { nextStep.getNo(),
                            nextStep.getItemName(), nextStep.getLocator() });
                }
            }

            //
            if (cmd.key.release && cmdRet >= 0) {
                cmd = null;
                break;
            } else {
                cmd = null;
            }

        }
        return ret;
    }

    @PostConstruct
    public void init() {
        if (!isDebug()) {
            return;
        }

        LOG.info("デバッグモードでテストを実行します。" + "実行を一時停止するにはEnterキーをタイプしてください。");

        executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Scanner scan = new Scanner(System.in);
                try {
                    while ((cmd = DebugCommand.readLine(scan.nextLine())) != null) {

                        if (cmd.key == CommandKey.START) {
                            LOG.info("テスト実行を再開します。");
                            setPaused(false);

                        } else if (cmd == DebugCommand.NA) {
                            if (!isPaused()) {
                                pause();
                            } else {
                                showUsage();
                            }
                        }
                    }
                } finally {
                    scan.close();
                }
            }
        });
    }

    public void pause() {
        LOG.info("テストスクリプトの実行を一時停止します。ブラウザの操作は可能です。" + "操作方法を表示するにはEnterキーをタイプしてください。");
        setPaused(true);
    }

    private void showUsage() {
        LOG.info(USAGE_DESC);
    }

    @PreDestroy
    public void destroy() {
        if (isDebug()) {
            executor.shutdownNow();
        }
    }

    public int getPauseSpan() {
        return pauseSpan;
    }

    public synchronized void setPauseSpan(int pauseSpan) {
        this.pauseSpan = pauseSpan;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
