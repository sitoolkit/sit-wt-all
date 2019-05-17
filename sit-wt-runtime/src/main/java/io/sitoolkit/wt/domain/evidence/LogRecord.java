/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.sitoolkit.wt.domain.evidence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.resource.MessageManager;

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
   * @param logger ロガー
   * @param position 要素位置
   * @param testStep テストステップ
   * @param pattern メッセージパターン
   * @param params メッセージパラメーター
   * @return 操作ログ
   */
  public static LogRecord create(SitLogger logger, ElementPosition position, TestStep testStep,
      MessagePattern pattern, Object... params) {

    Object[] newParams = new Object[] {testStep.getItemName(), testStep.getLocator()};
    newParams = ArrayUtils.addAll(newParams, params);

    return create(logger, position, testStep, pattern.getPattern(), newParams);
  }

  /**
   * 操作ログオブジェクトを作成します。
   *
   * @param logger ロガー
   * @param position 要素位置
   * @param testStep テストステップ
   * @param messageKey メッセージキー
   * @param params メッセージパラメーター
   * @return 操作ログ
   */
  public static LogRecord create(SitLogger logger, ElementPosition position, TestStep testStep,
      String messageKey, Object... params) {

    String msg = MessageManager.getMessage(messageKey, params);

    logger.infoMsg(msg);

    return new LogRecord(testStep.getNo(), msg, position);
  }

  /**
   * 操作ログオブジェクトを作成します。
   *
   * @param logger ロガー
   * @param logLevel ログレベル
   * @param testStep テストステップ
   * @param messageKey メッセージキー
   * @param params メッセージパラメーター
   * @return 操作ログ
   */
  public static LogRecord create(SitLogger logger, LogLevelVo logLevel, TestStep testStep,
      String messageKey, Object... params) {

    String msg = MessageManager.getMessage(messageKey, params);

    switch (logLevel) {
      case INFO:
        logger.infoMsg(msg);
        break;
      case DEBUG:
        logger.debugMsg(msg);
        break;
      case ERROR:
        logger.errorMsg(msg);
        break;
      case WARN:
        logger.warnMsg(msg);
        break;
      default:
        logger.infoMsg(msg);
    }

    String testStepNo = testStep == null ? "xxx" : testStep.getNo();

    return new LogRecord(testStepNo, msg);
  }

  /**
   * 操作ログオブジェクトを作成します。
   *
   * @param logger ロガー
   * @param testStep テストステップ
   * @param messagePattern メッセージパターン
   * @param params メッセージパラメーター
   * @return 操作ログ
   */
  public static LogRecord info(SitLogger logger, TestStep testStep, MessagePattern messagePattern,
      Object... params) {

    Object[] newParams = new Object[] {testStep.getItemName(), testStep.getLocator()};
    newParams = ArrayUtils.addAll(newParams, params);

    return info(logger, testStep, messagePattern.getPattern(), newParams);
  }

  /**
   * 操作ログオブジェクトを作成します。
   *
   * @param logger ロガー
   * @param testStep テストステップ
   * @param messageKey メッセージキー
   * @param params メッセージパラメーター
   * @return 操作ログ
   */
  public static LogRecord info(SitLogger logger, TestStep testStep, String messageKey,
      Object... params) {

    return create(logger, LogLevelVo.INFO, testStep, messageKey, params);
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
