package io.sitoolkit.wt.app.selenium2script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class SeleniumStepConverter implements ApplicationContextAware {

  protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

  protected ApplicationContext appCtx;

  private Map<String, String> seleniumIdeCommandMap;

  private Pattern screenshotPattern;

  public List<TestStep> convertTestScript(SeleniumTestScript seleniumTestScript, String caseNo) {
    List<TestStep> testStepList = new ArrayList<TestStep>();

    int stepNo = 1;

    for (SeleniumTestStep seleniumStep : seleniumTestScript.getTestStepList()) {
      TestStep sitStep = new TestStep();
      testStepList.add(sitStep);

      sitStep.setCurrentCaseNo(caseNo);
      sitStep.setNo(Integer.toString(stepNo++));

      // 操作
      sitStep.setOperationName(convertOperationName(seleniumStep));

      // ロケーター
      sitStep.setLocator(convertLocator(seleniumStep));

      // テストデータ
      setTestData(sitStep, caseNo, seleniumStep.getValue());

      // open操作の場合はseleniumScriptのbaseUrlを設定
      if ("open".equals(sitStep.getOperationName())) {
        String locatorValue = sitStep.getLocator().getValue();
        sitStep.getLocator().setValue(seleniumTestScript.getBaseUrl() + locatorValue);
      }

      // スクリーンショット
      sitStep.setScreenshotTiming(convertScreenshotTiming(seleniumStep));

    }
    return testStepList;
  }

  /**
   * TestStepのテストデータを設定します。
   *
   * @param testStep TestStep
   * @param caseNo ケース番号
   * @param value テストデータ
   */
  protected void setTestData(TestStep testStep, String caseNo, String value) {
    Map<String, String> testData = new HashMap<String, String>();
    String[] pair = StringUtils.split(value, "=");
    if (pair.length == 2) {
      testStep.setDataType(pair[0]);
      testData.put(caseNo, pair[1]);
    } else {
      if (StringUtils.isBlank(value)) {
        testData.put(caseNo, "y");
      } else {
        testData.put(caseNo, value);
      }
    }
    testStep.setTestData(testData);
  }

  protected String convertOperationName(SeleniumTestStep seleniumStep) {

    String operationName = seleniumIdeCommandMap.get(seleniumStep.getCommand());

    if (operationName == null) {
      if (appCtx.containsBeanDefinition(seleniumStep.getCommand() + "Operation")) {
        operationName = seleniumStep.getCommand();
      } else {
        log.info("selenium.command.unmatched", seleniumStep.getCommand());
      }
    }

    return operationName;

  }

  protected Locator convertLocator(SeleniumTestStep seleniumStep) {
    return Locator.build(seleniumStep.getTarget());
  }

  protected String convertScreenshotTiming(SeleniumTestStep seleniumStep) {
    String command = StringUtils.defaultString(seleniumStep.getCommand());
    return screenshotPattern.matcher(command).matches() ? "前" : "";
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.appCtx = applicationContext;
  }

  public Map<String, String> getSeleniumIdeCommandMap() {
    return seleniumIdeCommandMap;
  }

  public void setSeleniumIdeCommandMap(Map<String, String> seleniumIdeCommandMap) {
    this.seleniumIdeCommandMap = seleniumIdeCommandMap;
  }

  public void setScreenshotPattern(Pattern screenshotPattern) {
    this.screenshotPattern = screenshotPattern;
  }
}
