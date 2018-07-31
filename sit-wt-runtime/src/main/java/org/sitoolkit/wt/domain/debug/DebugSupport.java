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

import java.nio.file.Path;
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
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import lombok.Setter;

/**
 *
 * @author yuichi.kuwahara
 */
public class DebugSupport implements ApplicationListener<ApplicationEvent> {

    private static SitLogger LOG = SitLoggerFactory.getLogger(DebugSupport.class);

    private static final String USAGE_DESC = CommandKey.buildUsage();

    @Resource
    TestContext current;

    @Resource
    TestScriptDao dao;

    @Resource
    ApplicationContext appCtx;

    @Resource
    PropertyManager pm;

    @Setter
    DebugListener listener;

    /**
     * ポーズ中にスレッドをsleepする間隔(ミリ秒)
     */
    private int pauseSpan = 800;

    private ExecutorService executor;

    private DebugCommand cmd;

    private boolean paused = false;

    /**
     * テスト実行を継続する場合にtrueを返します。 また内部では、{@code TestContext}に次に実行すべき
     * {@code TestStep}
     * とテストステップインデックスを設定します。(テストステップインデックスはテストスクリプト内でのテストステップの順番です。)
     *
     * @return テスト実行を継続する場合にtrue
     */
    public boolean next() {
        final int currentIndex = current.getCurrentIndex();
        int nextIndex = currentIndex + 1;
        TestScript testScript = current.getTestScript();
        LOG.debug("debug.test", current);
        if (currentIndex < testScript.getTestStepCount()) {

            if (pm.isDebug()) {

                LOG.debug("debug.index", currentIndex, testScript.getTestStepCount());

                TestStep nexttStep = testScript.getTestStep(nextIndex);

                if (nexttStep != null && StringUtils.isNotEmpty(nexttStep.getBreakPoint())) {
                    LOG.info("debug.break.point");
                    pause();
                }
                nextIndex = getNextIndex(currentIndex);
            }

            if (nextIndex >= testScript.getTestStepCount()) {
                sendCaseEnd();
                return current.isContinued();
            }

            current.setCurrentIndex(nextIndex);
            current.setTestStep(current.getTestScript().getTestStep(nextIndex));
            sendStepStart();

            return true;
        } else {
            sendCaseEnd();
            return current.isContinued();
        }
    }

    // public synchronized boolean isDebug() {
    // if (debug == null) {
    // debug = getSysPropAsBoolean("sitwt.debug");
    // }
    // return debug;
    // }
    //
    // private boolean getSysPropAsBoolean(String key) {
    // return Boolean.parseBoolean(System.getProperty(key));
    // }

    /**
     * 次に実行すべきテストステップインデックスを返します。 また内部では以下の処理を行います。
     * <ul>
     * <li>テスト実行スレッドの待機(一時停止)
     * <li>一時停止中のテストスクリプトの変更の監視と再読込
     * <li>一時停止中の入力コマンドの制御
     * <li>実行前後のテストステップのコンソール表示
     * </ul>
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
        int retBefore = currentIndex;
        sendStepPausing(ret + 1);

        while (isPaused()) {

            if (ret != retBefore) {
                sendStepPausing(ret + 1);
                retBefore = ret;
            }

            try {
                Thread.sleep(getPauseSpan());
            } catch (InterruptedException e) {
                LOG.warn("thread.sleep.error", e);
            }

            TestScript testScript = current.getTestScript();
            if (testScript.isScriptFileChanged()) {
                LOG.info("script.file.changed");
                current.setTestScript(
                        dao.load(testScript.getScriptFile(), testScript.getSheetName(), false));
            }

            // コンソールから有効なコマンド入力があるまでループします。
            if (cmd == null || cmd.key == null) {
                continue;
            }

            final int cmdRet = cmd.execute(ret, current.getTestScript(), appCtx);

            if (cmdRet < 0) {
                LOG.info("cmd.error");
            } else {
                ret = cmdRet;

                if (current.getTestScript().getTestStep(ret) == null) {
                    LOG.info("test.step.end");
                    break;
                }

                LOG.info("empty");
                LOG.info("test.step.next.prev");
                for (int i = ret - 1; i <= ret + 1; i++) {
                    TestStep nextStep = current.getTestScript().getTestStep(i);
                    if (nextStep == null) {
                        continue;
                    }
                    String nextMark = i == ret + 1 ? " <- 次に実行" : "";
                    LOG.info("test.step.next", new Object[] { nextStep.getNo(),
                            nextStep.getItemName(), nextStep.getLocator(), nextMark });
                }
                LOG.info("empty");
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
        if (!pm.isDebug()) {
            return;
        }

        LOG.info("debug.execute");
        if (pm.isCli()) {
            LOG.info("enter.pause.click");
        }

        executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Scanner scan = new Scanner(System.in);
                try {
                    while ((cmd = DebugCommand.readLine(scan.nextLine())) != null) {

                        if (cmd.key == CommandKey.START) {
                            LOG.info("test.restart");
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

    public void restart(String stepNo) {
        LOG.info("test.restart");
        if (StringUtils.isEmpty(stepNo)) {
            cmd = new DebugCommand(CommandKey.START, "");
        } else {
            cmd = new DebugCommand(CommandKey.EXEC_STEP_NO, stepNo);
        }
        setPaused(false);
    }

    public void forward() {
        if (!isPaused()) {
            return;
        }
        cmd = new DebugCommand(CommandKey.FORWARD, "");
    }

    public void back() {
        if (!isPaused()) {
            return;
        }
        cmd = new DebugCommand(CommandKey.BACK, "");
    }

    public void checkLocator(String locatorStr) {
        cmd = new DebugCommand(CommandKey.LOC, locatorStr);
    }

    public void export() {
        cmd = new DebugCommand(CommandKey.EXPORT, "");
    }

    public void pause() {
        LOG.info("test.pause");
        if (pm.isCli()) {
            LOG.info("enter.click");
        }
        setPaused(true);
    }

    private void showUsage() {
        if (pm.isCli()) {
            LOG.info("show.usage", USAGE_DESC);
        }
    }

    @PreDestroy
    public void destroy() {
        if (pm.isDebug()) {
            executor.shutdownNow();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextClosedEvent) {
            sendClose();
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

    public void start() {
        sendStepStart();
    }

    private void sendStepPausing(int nextStepIndex) {
        Path path = current.getTestScript().getScriptFile().toPath();
        int caseIndex = current.getTestScript().getCaseNoMap().get(current.getCaseNo());
        listener.onPause(path, nextStepIndex, caseIndex);
    }

    private void sendStepStart() {
        Path path = current.getTestScript().getScriptFile().toPath();
        int caseIndex = current.getTestScript().getCaseNoMap().get(current.getCaseNo());
        int stepIndex = current.getCurrentIndex();
        listener.onStepStart(path, stepIndex, caseIndex);
    }

    private void sendCaseEnd() {
        Path path = current.getTestScript().getScriptFile().toPath();
        int caseIndex = current.getTestScript().getCaseNoMap().get(current.getCaseNo());
        listener.onCaseEnd(path, caseIndex);
    }

    private void sendClose() {
        listener.onClose();
    }
}
