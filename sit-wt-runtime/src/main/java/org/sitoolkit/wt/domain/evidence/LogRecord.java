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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

/**
 * このクラスは、操作ログの構成単位となるエンティティです。 1回の操作、または1つのスクリーンショットを1インスタンスとして保持します。
 */
public class LogRecord {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * ステップNo
     */
    private String no = "";
    /**
     * タイムスタンプ
     */
    private String timestamp;
    /**
     * ログ文字列
     */
    private String log;
    /**
     * スクリーンショットファイル
     */
    private String filePath;

    private List<ElementPosition> positions = new ArrayList<>();

    /**
     * スクリーンショット
     */
    private Screenshot screenshot;

    /**
     * ログレベル
     */
    private LogLevelVo logLevel;

    public LogRecord() {
        this.timestamp = DATE_FORMAT.format(new Date());
    }

    public LogRecord(String no, String log) {
        this();
        this.no = no;
        this.log = log;
    }

    private LogRecord(String no, String log, ElementPosition... positions) {
        this(no, log);
        this.positions = new ArrayList<>();

        for (ElementPosition position : positions) {
            if (ElementPosition.EMPTY != position) {
                position.setNo(no);
                this.positions.add(position);
            }
        }
    }

    /**
     * 次のメッセージを持つ操作ログオブジェクトを作成します。
     * 
     * 
     * @param logger
     *            ロガー
     * @param position
     *            要素位置
     * @param testStep
     *            テストステップ
     * @param pattern
     *            メッセージパターン
     * @param params
     *            メッセージパラメーター
     * @return 操作ログ
     */
    public static LogRecord create(Logger logger, ElementPosition position, TestStep testStep,
            MessagePattern pattern, Object... params) {

        Object[] newParams = new Object[] { testStep.getItemName(), testStep.getLocator() };
        newParams = ArrayUtils.addAll(newParams, params);

        return create(logger, position, testStep, pattern.getPattern(), newParams);
    }

    /**
     * 操作ログオブジェクトを作成します。
     * 
     * @param logger
     *            ロガー
     * @param position
     *            要素位置
     * @param testStep
     *            テストステップ
     * @param messagePattern
     *            メッセージパターン
     * @param params
     *            メッセージパラメーター
     * @return 操作ログ
     */
    public static LogRecord create(Logger logger, ElementPosition position, TestStep testStep,
            String messagePattern, Object... params) {

        String msg = log(messagePattern, params);

        logger.info(msg);

        return new LogRecord(testStep.getNo(), msg, position);
    }

    /**
     * 操作ログオブジェクトを作成します。
     * 
     * @param logger
     *            ロガー
     * @param logLevel
     *            ログレベル
     * @param testStep
     *            テストステップ
     * @param messagePattern
     *            メッセージパターン
     * @param params
     *            メッセージパラメーター
     * @return 操作ログ
     */
    public static LogRecord create(Logger logger, LogLevelVo logLevel, TestStep testStep,
            String messagePattern, Object... params) {

        String msg = log(messagePattern, params);

        switch (logLevel) {
            case INFO:
                logger.info(msg);
                break;
            case DEBUG:
                logger.debug(msg);
                break;
            case ERROR:
                logger.error(msg);
                break;
            case WARN:
                logger.warn(msg);
                break;
            default:
                logger.info(msg);
        }

        String testStepNo = testStep == null ? "xxx" : testStep.getNo();

        return new LogRecord(testStepNo, msg);
    }

    /**
     * 操作ログオブジェクトを作成します。
     * 
     * @param logger
     *            ロガー
     * @param testStep
     *            テストステップ
     * @param messagePattern
     *            メッセージパターン
     * @param params
     *            メッセージパラメーター
     * @return 操作ログ
     */
    public static LogRecord info(Logger logger, TestStep testStep, MessagePattern messagePattern,
            Object... params) {

        Object[] newParams = new Object[] { testStep.getItemName(), testStep.getLocator() };
        newParams = ArrayUtils.addAll(newParams, params);

        return info(logger, testStep, messagePattern.getPattern(), newParams);
    }

    /**
     * 操作ログオブジェクトを作成します。
     * 
     * @param logger
     *            ロガー
     * @param testStep
     *            テストステップ
     * @param messagePattern
     *            メッセージパターン
     * @param params
     *            メッセージパラメーター
     * @return 操作ログ
     */
    public static LogRecord info(Logger logger, TestStep testStep, String messagePattern,
            Object... params) {

        return create(logger, LogLevelVo.INFO, testStep, messagePattern, params);
    }

    private static String log(String messagePattern, Object... params) {
        return MessageFormatter.arrayFormat(messagePattern, params).getMessage();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public LogLevelVo getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevelVo logLevel) {
        this.logLevel = logLevel;
    }

    public Screenshot getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(Screenshot screenshot) {
        this.screenshot = screenshot;
    }

    public List<ElementPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<ElementPosition> positions) {
        this.positions = positions;
    }

}
