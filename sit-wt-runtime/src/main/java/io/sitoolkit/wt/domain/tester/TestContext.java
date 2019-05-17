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
package io.sitoolkit.wt.domain.tester;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestStep;

/**
 *
 * @author yuichi.kuwahara
 */
public class TestContext {

  /**
   * 現在実行中のテストスクリプト
   */
  private TestScript testScript;

  /**
   * 現在実行中のテストステップ
   */
  private TestStep testStep;

  /**
   * 現在実行中のスクリプト名
   */
  private String scriptName;

  /**
   * 現在実行中のケース番号
   */
  private String caseNo;

  /**
   * 退避用のケース番号
   */
  private String caseNoTmp;

  /**
   * 退避用のテストスクリプト
   */
  private TestScript testScriptTmp;

  /**
   * TestContextのイベントにアタッチするためのインターフェース
   */
  private TestContextListener testContextListener;

  /**
   * 現在実行中のテストスクリプトを指すTestScriptListのインデックス
   */
  private int currentIndex;

  /**
   * 退避用のインデックス
   */
  private int indexTmp;

  /**
   * ブラウザのウィンドウの位置とサイズを表す矩形
   */
  private Rectangle windowRect;
  /**
   *
   */
  private Map<String, Object> params = new HashMap<String, Object>();

  public boolean isContinued() {
    if (testContextListener == null) {
      return false;
    } else {
      testContextListener.onEnd(this);
      return true;
    }
  }

  /**
   * 現在実行中の以下の項目を退避領域に退避します。
   *
   * <ul>
   * <li>テストスクリプト
   * <li>ケース番号
   * <li>テストステップインデックス
   * </ul>
   */
  public void backup() {
    testScriptTmp = testScript;
    indexTmp = currentIndex;
    caseNoTmp = caseNo;
  }

  public void restore() {
    testScript = testScriptTmp;
    currentIndex = indexTmp + 1;
    testStep = testScript.getTestStep(currentIndex);
    caseNo = caseNoTmp;
  }

  /**
   * 以下の項目を初期状態にリセットします。
   *
   * <ul>
   * <li>テストステップ
   * <li>テストステップインデックス
   * </ul>
   */
  public void reset() {
    currentIndex = 0;
    testStep = testScript.getTestStep(0);
  }

  public TestScript getTestScript() {
    return testScript;
  }

  public void setTestScript(TestScript testScript) {
    this.testScript = testScript;
  }

  public TestStep getTestStep() {
    return testStep;
  }

  public void setTestStep(TestStep testStep) {
    this.testStep = testStep;
  }

  public String getScriptName() {
    return scriptName;
  }

  public void setScriptName(String scriptName) {
    this.scriptName = scriptName;
  }

  public String getCaseNo() {
    return caseNo;
  }

  public void setCaseNo(String caseNo) {
    this.caseNo = caseNo;
  }

  public TestScript getTestScriptTmp() {
    return testScriptTmp;
  }

  public void setTestScriptTmp(TestScript testScriptTmp) {
    this.testScriptTmp = testScriptTmp;
  }

  public TestContextListener getTestContextListener() {
    return testContextListener;
  }

  public void setTestContextListener(TestContextListener testContextListener) {
    this.testContextListener = testContextListener;
  }

  public void addParam(String key, Object value) {
    params.put(key, value);
  }

  public <T> T getParam(String key) {
    return (T) params.get(key);
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  public String getTestStepNo() {
    return testStep == null ? "noTestStepNo" : testStep.getNo();
  }

  public String getItemName() {
    return testStep == null ? "noItemName" : testStep.getItemName();
  }

  public String getScreenshotTiming() {
    return testStep == null ? "noScreenshotTiming" : testStep.getScreenshotTiming().getLabel();
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public void setCurrentIndex(int currentIndex) {
    this.currentIndex = currentIndex;
  }

  public Rectangle getWindowRect() {
    return windowRect;
  }

  public void setWindowRect(Rectangle windowRect) {
    this.windowRect = windowRect;
  }

  public void setWindowRect(int x, int y, int w, int h) {
    setWindowRect(new Rectangle(x, y, w, h));
  }

  public int getIndexTmp() {
    return indexTmp;
  }

  public void setIndexTmp(int indexTmp) {
    this.indexTmp = indexTmp;
  }

}
