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

  public static InputRule getNoRule() {
    return FreeRule.getInstance();
  }

  public static InputRule getItemNameRule() {
    return FreeRule.getInstance();
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
      return DisabledRule.getInstance();
    } else {
      return FreeRule.getInstance();
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
    return FreeRule.getInstance();
  }

  public static InputRule getTestDataRule(String operationName) {
    List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
    switch (dataTypes.get(0)) {
      case "ok_cancel":
        return OkCancelRule.getInstance();
      case "na":
        return DisabledRule.getInstance();
      default:
        return FreeRule.getInstance();
    }
  }
}
