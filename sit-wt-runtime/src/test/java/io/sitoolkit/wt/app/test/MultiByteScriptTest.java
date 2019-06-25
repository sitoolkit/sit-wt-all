package io.sitoolkit.wt.app.test;

import org.junit.Test;
import io.sitoolkit.wt.domain.tester.TestBase;

public class MultiByteScriptTest extends TestBase {

  @Test
  public void testMultiByteCase() {

    boolean isParallel = Boolean.getBoolean("sitwt.parallel");
    boolean isEvidenceOpen = Boolean.getBoolean("sitwt.open-evidence");
    TestRunner testRunner = new TestRunner();

    testRunner.runScript(getTestScriptPath(), isParallel, isEvidenceOpen);
  }

  @Override
  protected String getTestScriptPath() {
    return "src/test/resources/テスト-スクリプト(サンプル).csv";
  }

  @Override
  protected String getSheetName() {
    return "TestScript";
  }
}
