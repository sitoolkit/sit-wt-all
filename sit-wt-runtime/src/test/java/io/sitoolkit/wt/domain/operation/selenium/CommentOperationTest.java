package io.sitoolkit.wt.domain.operation.selenium;

import org.junit.Test;

import io.sitoolkit.wt.domain.tester.TestBase;

public class CommentOperationTest extends TestBase {

  @Test
  public void test001() {
    test();
  }

  @Override
  protected String getTestScriptPath() {
    return "src/test/resources/selenium/CommentTestScript.csv";
  }

  @Override
  protected String getSheetName() {
    return "TestScript";
  }
}
