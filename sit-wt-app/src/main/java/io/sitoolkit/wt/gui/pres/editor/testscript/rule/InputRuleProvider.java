package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.stream.Stream;
import io.sitoolkit.wt.domain.testscript.TestStepInputType;

public class InputRuleProvider {

  private static final InputRule NOTHING = NothingRule.getInstance();

  public static InputRule getNoRule() {
    return NOTHING;
  }

  public static InputRule getItemNameRule() {
    return NOTHING;
  }

  public static InputRule getOperationNameRule() {
    List<String> operationNames =
        Stream.of(TestStepInputType.values())
            .map(TestStepInputType::getOperationName)
            .collect(toList());

    return new ValueListRule(operationNames);
  }

  public static InputRule getLocatorTypeRule(String operationName) {
    // TODO Auto-generated method stub
    return "fix".equals(operationName) ? new OneValueRule("fixed-locatorType") : NOTHING;
  }

  public static InputRule getLocatorRule(String operationName) {
    // TODO Auto-generated method stub
    return "fix".equals(operationName) ? new OneValueRule("fixed-locator") : NOTHING;
  }

  public static InputRule getDataTypeRule(String operationName) {
    // TODO Auto-generated method stub
    return "fix".equals(operationName) ? new OneValueRule("fixed-dataType") : NOTHING;
  }

  public static InputRule getScreenshotTimingRule(String operationName) {
    // TODO Auto-generated method stub
    return "fix".equals(operationName) ? new OneValueRule("前") : NOTHING;
  }

  public static InputRule getBreakpointRule() {
    // TODO Auto-generated method stub
    return NothingRule.getInstance();
  }

  public static InputRule getTestDataRule(String operationName) {
    return "fix".equals(operationName) ? new OneValueRule("fixed-testData") : NOTHING;
  }
}
