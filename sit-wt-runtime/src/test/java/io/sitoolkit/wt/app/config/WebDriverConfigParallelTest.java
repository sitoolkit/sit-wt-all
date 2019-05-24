package io.sitoolkit.wt.app.config;

import org.junit.Test;
import io.sitoolkit.wt.domain.tester.TestBase;

public class WebDriverConfigParallelTest extends TestBase {

  @Test
  public void test001() {
    test();
  }

  @Test
  public void test002() {
    test();
  }

  @Test
  public void test003() {
    test();
  }

  @Test
  public void test004() {
    test();
  }

  @Override
  protected String getTestScriptPath() {
    return "src/test/resources/WebDriverConfigParallelTestScript.csv";
  }

  @Override
  protected String getSheetName() {
    return "TestScript";
  }

}
