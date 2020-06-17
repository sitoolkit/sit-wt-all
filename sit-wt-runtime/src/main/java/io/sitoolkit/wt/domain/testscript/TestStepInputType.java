package io.sitoolkit.wt.domain.testscript;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

public enum TestStepInputType {

  //@formatter:off
  na("", Locator.Type.na, DataType.na),
  choose(Locator.Type.SELECTOR_TYPES, DataType.SELECT_TYPES),
  click(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.execution),
  comment(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.value),
  dbverify(Locator.Type.sql_file, DataType.verification_value),
  dialog(Locator.Type.na, DataType.ok_cancel),
  download(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.execution),
  drawLine(Locator.Type.SELECTOR_TYPES, DataType.coordinates),
  exec(Locator.Type.os_command, DataType.execution),
  GOTO("goto", Locator.Type.case_no, DataType.execution),
  include(Locator.Type.testscript_file, DataType.execution),
  input(Locator.Type.SELECTOR_TYPES, DataType.input_value),
  key(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.key_operation),
  open(Locator.Type.url, DataType.execution),
  select(Locator.Type.SELECTOR_TYPES, DataType.SELECT_TYPES),
  setWindowSize(Locator.Type.na, DataType.window_size),
  spin(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.verification_value),
  startApp(Locator.Type.url, DataType.execution),
  store(Locator.Type.variable, DataType.store_value),
  storeElementIndex(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.variable_name),
  storeElementValue(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.variable_name),
  switchFrame(Locator.Type.SELECTOR_TYPES, DataType.execution),
  switchWindow(Locator.Type.TITLE_TYPES, DataType.execution),
  verify(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.verification_value),
  verifyAttribute(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.verification_value),
  verifySelect(Locator.Type.SELECTOR_TYPES, DataType.verification_value),
  wait(Locator.Type.SELECTOR_WITH_LINK_TYPES, DataType.verification_value),
  ;
  //@formatter:on

  @Getter
  String operationName = name();

  @Getter
  List<String> locatorTypes;

  @Getter
  List<String> dataTypes;

  private TestStepInputType(List<Locator.Type> locatorTypes, List<DataType> dataTypes) {
    this.locatorTypes =
        locatorTypes.stream().map(Locator.Type::getLabel).collect(Collectors.toList());
    this.dataTypes = dataTypes.stream().map(DataType::name).collect(Collectors.toList());
  }

  private TestStepInputType(List<Locator.Type> locatorTypes, DataType dataType) {
    this.locatorTypes =
        locatorTypes.stream().map(Locator.Type::getLabel).collect(Collectors.toList());
    this.dataTypes = Arrays.asList(dataType.name());
  }

  private TestStepInputType(Locator.Type locatorType, DataType dataType) {
    this.locatorTypes = Arrays.asList(locatorType.getLabel());
    this.dataTypes = Arrays.asList(dataType.name());
  }

  private TestStepInputType(String operationName, Locator.Type locatorType, DataType dataType) {
    this(locatorType, dataType);
    this.operationName = operationName;
  }

  public static TestStepInputType decode(String operationName) {
    for (TestStepInputType type : values()) {
      if (type.getOperationName().equals(operationName)) {
        return type;
      }
    }
    return na;
  }

}
