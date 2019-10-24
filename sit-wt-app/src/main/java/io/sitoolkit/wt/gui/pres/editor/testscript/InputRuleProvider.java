package io.sitoolkit.wt.gui.pres.editor.testscript;

public class InputRuleProvider {

  public static InputRule getNoRule() {
    // TODO Auto-generated method stub
    return InputRule.NO_RULE;
  }

  public static InputRule getItemNameRule() {
    // TODO Auto-generated method stub
    return InputRule.NO_RULE;
  }

  public static InputRule getOperationNameRule() {
    // TODO Auto-generated method stub
    return InputRule.NO_RULE;
  }

  public static InputRule getLocatorTypeRule(String operationName) {
    // TODO Auto-generated method stub
    return "fix".equals(operationName) ? new OneValueRule("fixed-locatorType") : InputRule.NO_RULE;
  }

  public static InputRule getLocatorRule(String operationName) {
    // TODO Auto-generated method stub
    return "fix".equals(operationName) ? new OneValueRule("fixed-locator") : InputRule.NO_RULE;
  }

  public static InputRule getDataTypeRule(String operationName) {
    // TODO Auto-generated method stub
    return "fix".equals(operationName) ? new OneValueRule("fixed-dataType") : InputRule.NO_RULE;
  }

  public static InputRule getScreenshotTimingRule(String operationName) {
    // TODO Auto-generated method stub
    return "fix".equals(operationName) ? new OneValueRule("前") : InputRule.NO_RULE;
  }

  public static InputRule getBreakpointRule() {
    // TODO Auto-generated method stub
    return InputRule.NO_RULE;
  }

  public static InputRule getTestDataRule(String operationName) {
    return "fix".equals(operationName) ? new OneValueRule("fixed-testData") : InputRule.NO_RULE;
  }
}
