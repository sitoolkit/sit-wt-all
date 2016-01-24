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
package org.sitoolkit.wt.domain.evidence;

import javax.annotation.Resource;

import org.sitoolkit.wt.domain.tester.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * このクラスは、テストの操作ログを表すリポジトリです。 操作ログは、カレントディレクトリ以下に以下の階層で出力します。
 * 
 * <pre>
 * +-opelog_yyyyMMddhhmmss + -img + -case_nnn_mmm_itemName.png + -case_nnn.html
 * </pre>
 *
 * @author yuichi.kuwahara
 */
public class OperationLog {

    private static final Logger LOG = LoggerFactory.getLogger(OperationLog.class);

    @Resource
    TestContext current;

    @Resource
    EvidenceManager em;

    @Resource
    ScreenshotTaker screenshotTaker;

    private Evidence currentEvidence;

    /**
     * 定型メッセージで操作ログを出力します。
     * 
     * @param log
     *            ロガー
     * @param verb
     *            ログメッセージの動作を表す単語
     * @param pos
     */
    public void info(Logger log, String verb, ElementPosition pos) {
        info(log, pos, "{}({})を{}します", current.getTestStep().getItemName(),
                current.getTestStep().getLocator(), verb);
    }

    /**
     * 定型メッセージで操作ログを出力します。
     * 
     * @param log
     *            ロガー
     * @param object
     *            ログメッセージの目的語
     * @param verb
     *            ログメッセージの動作を表す単語
     * @param pos
     */
    public void info(Logger log, String object, String verb, ElementPosition pos) {
        info(log, pos, "{}({})に[{}]を{}します", current.getTestStep().getItemName(),
                current.getTestStep().getLocator(), object, verb);
    }

    public void info(Logger log, ElementPosition pos, String messagePattern, Object... params) {
        String msg = MessageFormatter.arrayFormat(messagePattern, params).getMessage();
        outLogAndAddRecord(log, msg, pos, LogLevelVo.INFO);
    }

    public void warn(Logger log, String messagePattern, Object... params) {
        String msg = MessageFormatter.arrayFormat(messagePattern, params).getMessage();
        outLogAndAddRecord(log, msg, null, LogLevelVo.WARN);
    }

    void outLogAndAddRecord(Logger log, String msg, ElementPosition pos, LogLevelVo logLevel) {

        LogRecord record = new LogRecord();

        switch (logLevel) {
        case DEBUG:
            log.debug(msg);
            break;
        case INFO:
            log.info(msg);
            break;
        case WARN:
            log.warn(msg);
            break;
        case ERROR:
            log.error(msg);
            break;
        default:
            // NOP
        }

        record.setLogLevel(logLevel);
        record.setNo(current.getTestStep().getNo());
        record.setLog(msg);

        currentEvidence.addLogRecord(record);

        addPosition(pos);
    }

    public void addPosition(ElementPosition pos) {
        if (pos != null && pos != ElementPosition.EMPTY) {
            pos.setNo(current.getTestStep().getNo());
            currentEvidence.addElementPosition(pos);
        }
    }

    public void addScreenshot(ScreenshotTiming timing) {
        Screenshot screenshot = screenshotTaker.get(timing);
        currentEvidence.addScreenshot(screenshot, current.getScreenshotTiming());
        em.moveScreenshot(currentEvidence, current.getTestStepNo(), current.getItemName());
    }

    public void endScript() {
        em.flushEvidence(currentEvidence);

    }

    public void moveLogFile() {
        em.moveLogFile();
    }

    /**
     * エラーログを出力します。
     */
    public void error(Logger log, String msg) {

        LogRecord record = new LogRecord();

        log.error(msg);

        record.setNo(current.getTestStepNo());
        record.setLog(msg);
        record.setLogLevel(LogLevelVo.ERROR);
        currentEvidence.addLogRecord(record);

    }

    public void beginScript() {
        currentEvidence = em.createEvidence(current.getScriptName(), current.getCaseNo());
    }

    public void endStep() {
        currentEvidence.commitScreenshot();
    }

}
