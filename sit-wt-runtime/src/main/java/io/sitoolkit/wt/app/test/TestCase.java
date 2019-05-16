package io.sitoolkit.wt.app.test;

import org.apache.commons.lang3.StringUtils;

public class TestCase {
  private String scriptPath;

  private String sheetName;

  private String caseNo;

  /**
   * デフォルトコンストラクタ
   *
   */
  public TestCase() {
    this.scriptPath = "";
    this.sheetName = "TestScript";
    this.caseNo = "";
  }

  /**
   * パラメータ指定のコンストラクタ
   *
   * @param initScriptPath テストスクリプトのパス
   * @param initSheetName テスト対象のシート名
   * @param initCaseNo テスト対象のケースNo
   */
  public TestCase(String initScriptPath, String initSheetName, String initCaseNo) {
    this.scriptPath = initScriptPath;
    this.sheetName = initSheetName;
    this.caseNo = initCaseNo;
  }

  /**
   * テスト条件の一括指定
   *
   * @param testCondition テストケース文字列(scriptPath!sheetName#caseNo)
   */
  public static TestCase parse(String testCondition) {
    String scriptPath = "";
    String sheetName = "";
    String caseNo = "";

    if (testCondition.contains("!")) {
      scriptPath = StringUtils.substringBefore(testCondition, "!");
      sheetName = StringUtils.substringAfter(StringUtils.substringBefore(testCondition, "#"), "!");
      caseNo = StringUtils.substringAfter(testCondition, "#");
    } else {
      scriptPath = StringUtils.substringBefore(testCondition, "#");
      sheetName = "TestScript";
      caseNo = StringUtils.substringAfter(testCondition, "#");
    }

    return new TestCase(scriptPath, sheetName, caseNo);
  }

  public String getScriptPath() {
    return scriptPath;
  }

  public void setScriptPath(String scriptPath) {
    this.scriptPath = scriptPath;
  }

  public String getSheetName() {
    return sheetName;
  }

  public void setSheetName(String sheetName) {
    this.sheetName = sheetName;
  }

  public String getCaseNo() {
    return caseNo;
  }

  public void setCaseNo(String caseNo) {
    this.caseNo = caseNo;
  }
}
