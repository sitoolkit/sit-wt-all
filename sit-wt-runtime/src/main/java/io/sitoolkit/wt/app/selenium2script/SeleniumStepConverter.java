package io.sitoolkit.wt.app.selenium2script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import lombok.Setter;

public class SeleniumStepConverter implements ApplicationContextAware {

  protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

  protected ApplicationContext appCtx;

  @Setter
  private Map<String, String> seleniumIdeCommandMap;

  @Setter
  private Pattern screenshotPattern;

  private static final List<String> COMMANDS_TO_SKIP =
      Collections.unmodifiableList(Arrays.asList("chooseOkOnNextConfirmation",
          "chooseCancelOnNextConfirmation", "assertConfirmation", "storeWindowHandle"));

  private static final List<String> CONFIRMATION_COMMANDS =
      Collections.unmodifiableList(Arrays.asList("webdriverChooseOkOnVisibleConfirmation",
          "webdriverChooseCancelOnVisibleConfirmation"));

  public List<TestStep> convertTestScript(SeleniumTestScript seleniumTestScript, String caseNo) {
    List<TestStep> testStepList = new ArrayList<TestStep>();

    int stepNo = 1;

    for (SeleniumTestStep seleniumStep : seleniumTestScript.getTestStepList()) {
      String command = seleniumStep.getCommand();
      if (COMMANDS_TO_SKIP.contains(command)) {
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
      if (CONFIRMATION_COMMANDS.contains(command)) {
        sitStep.setTestData(convertConfirmData(caseNo, command));
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
    String data = "webdriverChooseOkOnVisibleConfirmation".equals(command) ? "ok" : "cancel";
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
    if (TestStepInputType.na.equals(inputType)) {
      log.info("selenium.command.unmatched", seleniumStep.getCommand());
      return null;
    } else {
      return inputType;
    }

  }

  protected Locator convertLocator(TestStepInputType inputType, SeleniumTestScript seleniumScript,
      SeleniumTestStep seleniumStep) {
    String target = seleniumStep.getTarget();

    if (TestStepInputType.open.equals(inputType)) {
      String url = SitPathUtils.buildUrl(seleniumScript.getBaseUrl(), target);
      return Locator.build(url);

    } else if (inputType.getLocatorTypes().contains(Locator.Type.link.name())
        && target.startsWith("linkText=")) {
      return Locator.build(Locator.Type.link.toString(),
          StringUtils.removeStart(target, "linkText="));

    } else if (TestStepInputType.switchWindow.equals(inputType)) {
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

}
