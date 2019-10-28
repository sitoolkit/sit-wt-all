package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import static java.util.stream.Collectors.toList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import io.sitoolkit.wt.domain.testscript.ScreenshotTiming;
import io.sitoolkit.wt.domain.testscript.TestStepInputType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InputRuleProvider {

  @Getter private final InputRule noRule = FreeRule.getInstance();
  @Getter private final InputRule itemNameRule = FreeRule.getInstance();
  @Getter private final InputRule operationNameRule = createOperationNameRule();

  @Getter
  private final InputRule screenshotTimingRule = new ValueListRule(ScreenshotTiming.getLabels());

  @Getter private final InputRule breakpointRule = DisabledRule.getInstance();

  // operationName -> input rule
  private final Map<String, InputRule> locatorTypeRules = new HashMap<>();
  private final Map<String, InputRule> locatorRules = new HashMap<>();
  private final Map<String, InputRule> dataTypeRules = new HashMap<>();
  private final Map<String, InputRule> testDataRules = new HashMap<>();

  public InputRule getLocatorTypeRule(String operationName) {
    return locatorTypeRules.computeIfAbsent(operationName, this::createLocatorTypeRule);
  }

  public InputRule getLocatorRule(String operationName) {
    return locatorRules.computeIfAbsent(operationName, this::createLocatorRule);
  }

  public InputRule getDataTypeRule(String operationName) {
    return dataTypeRules.computeIfAbsent(operationName, this::createDataTypeRule);
  }

  public InputRule getTestDataRule(String operationName) {
    return testDataRules.computeIfAbsent(operationName, this::createTestDataRule);
  }

  public static InputRule createOperationNameRule() {
    List<String> operationNames =
        Stream.of(TestStepInputType.values())
            .map(TestStepInputType::getOperationName)
            .collect(toList());
    return new ValueListRule(operationNames);
  }

  public InputRule createLocatorTypeRule(String operationName) {
    List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
    return new ValueListRule(locatorTypes);
  }

  public InputRule createLocatorRule(String operationName) {
    List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
    if (locatorTypes.size() == 1 && locatorTypes.get(0).equals("")) {
      return DisabledRule.getInstance();
    } else {
      return FreeRule.getInstance();
    }
  }

  public InputRule createDataTypeRule(String operationName) {
    List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
    return new ValueListRule(dataTypes);
  }

  public InputRule createTestDataRule(String operationName) {
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
