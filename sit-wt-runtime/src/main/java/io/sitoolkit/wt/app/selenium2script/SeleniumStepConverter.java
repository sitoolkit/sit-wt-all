package io.sitoolkit.wt.app.selenium2script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.ScreenshotTiming;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.domain.testscript.TestStepInputType;
import io.sitoolkit.wt.infra.SitPathUtils;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class SeleniumStepConverter implements ApplicationContextAware {

  protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

  protected ApplicationContext appCtx;

  private Map<String, String> seleniumIdeCommandMap;

  private Pattern screenshotPattern;

  private final List<String> autoInsertedCommands = Arrays.asList(new String[] {
      "chooseOkOnNextConfirmation", "chooseCancelOnNextConfirmation", "storeWindowHandle"});

  public List<TestStep> convertTestScript(SeleniumTestScript seleniumTestScript, String caseNo) {
    List<TestStep> testStepList = new ArrayList<TestStep>();

    int stepNo = 1;

    Iterator<SeleniumTestStep> iterator = seleniumTestScript.getTestStepList().iterator();

    while (iterator.hasNext()) {
      SeleniumTestStep seleniumStep = iterator.next();
      if (autoInsertedCommands.contains(seleniumStep.getCommand())) {
        continue;
      }

      TestStep sitStep = new TestStep();
      testStepList.add(sitStep);

      sitStep.setCurrentCaseNo(caseNo);
      sitStep.setNo(Integer.toString(stepNo++));

      TestStepInputType inputType = convertInputType(seleniumStep);
      if (inputType == null) {
        sitStep.setLocator(Locator.build(null));
        continue;
      }

      sitStep.setOperationName(inputType.getOperationName());
      sitStep.setLocator(convertLocator(inputType, seleniumTestScript, seleniumStep));
      sitStep.setScreenshotTiming(convertScreenshotTiming(seleniumStep));

      String value = seleniumStep.getValue();
      if (seleniumStep.getCommand().equals("assertConfirmation")) {
        sitStep.setTestData(convertConfirmData(caseNo, iterator.next().getCommand()));
      } else {
        setTestData(sitStep, inputType, caseNo, value);
      }
    }

    return testStepList;
  }

  protected Map<String, String> createTestData(String caseNo, String data) {
    Map<String, String> testData = new HashMap<String, String>();
    testData.put(caseNo, data);
    return testData;
  }

  protected Map<String, String> convertConfirmData(String caseNo, String command) {
    String data = command.equals("webdriverChooseOkOnVisibleConfirmation") ? "ok" : "cancel";
    return createTestData(caseNo, data);
  }

  protected void setTestData(TestStep testStep, TestStepInputType inputType, String caseNo,
      String value) {
    String data;
    if (inputType.getDataTypes().size() >= 2) {
      String[] pair = StringUtils.split(value, "=");
      testStep.setDataType(pair[0]);
      data = pair[1];
    } else {
      data = StringUtils.isBlank(value) ? "y" : value;
    }

    testStep.setTestData(createTestData(caseNo, data));
  }

  protected TestStepInputType convertInputType(SeleniumTestStep seleniumStep) {

    String operationName = seleniumIdeCommandMap.get(seleniumStep.getCommand());
    if (operationName == null) {
      operationName = seleniumStep.getCommand();
    }

    TestStepInputType inputType = TestStepInputType.decode(operationName);
    if (inputType.equals(TestStepInputType.na)) {
      log.info("selenium.command.unmatched", seleniumStep.getCommand());
      return null;
    } else {
      return inputType;
    }

  }

  protected Locator convertLocator(TestStepInputType inputType, SeleniumTestScript seleniumScript,
      SeleniumTestStep seleniumStep) {
    String target = seleniumStep.getTarget();

    if (inputType.equals(TestStepInputType.open)) {
      String url = SitPathUtils.buildUrl(seleniumScript.getBaseUrl(), target);
      return Locator.build(url);

    } else if (inputType.getLocatorTypes().contains(Locator.Type.link.name())
        && target.startsWith("linkText=")) {
      return Locator.build(Locator.Type.link.toString(),
          StringUtils.removeStart(target, "linkText="));

    } else if (inputType.equals(TestStepInputType.switchWindow)) {
      return Locator.build(null);
    }

    return Locator.build(target);
  }

  protected String convertScreenshotTiming(SeleniumTestStep seleniumStep) {
    String command = StringUtils.defaultString(seleniumStep.getCommand());
    return screenshotPattern.matcher(command).matches() ? ScreenshotTiming.BEFORE.getLabel() : "";
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
