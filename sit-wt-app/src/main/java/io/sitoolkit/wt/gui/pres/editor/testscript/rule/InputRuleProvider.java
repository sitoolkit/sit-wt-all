package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.stream.Stream;
import io.sitoolkit.wt.domain.testscript.ScreenshotTiming;
import io.sitoolkit.wt.domain.testscript.TestStepInputType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
    List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
    return new ValueListRule(locatorTypes);
  }

  public static InputRule getLocatorRule(String operationName) {
    List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
    if (locatorTypes.size() == 1 && locatorTypes.get(0).equals("")) {
      return OneValueRule.BLANK_VALUE_RULE;
    } else {
      return NOTHING;
    }
  }

  public static InputRule getDataTypeRule(String operationName) {
    List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
    return new ValueListRule(dataTypes);
  }

  public static InputRule getScreenshotTimingRule() {
    // TODO Create ValueListRule instance only once and use it
    return new ValueListRule(ScreenshotTiming.getLabels());
  }

  public static InputRule getBreakpointRule() {
    return NOTHING;
  }

  public static InputRule getTestDataRule(String operationName) {
    List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
    switch (dataTypes.get(0)) {
      case "ok_cancel":
        return OkCancelRule.getInstance();
      case "na":
        return OneValueRule.BLANK_VALUE_RULE;
      default:
        return NOTHING;
    }
  }
}
