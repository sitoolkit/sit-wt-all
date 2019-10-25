package io.sitoolkit.wt.gui.pres.editor.testscript;

import static java.util.stream.Collectors.toList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.gui.pres.editor.testscript.rule.InputRule;
import io.sitoolkit.wt.gui.pres.editor.testscript.rule.InputRuleProvider;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class ScriptEditorRow {

  private static final String TEST_DATA_PROP_PREFIX = "case_";

  // propertyName -> property
  private Map<String, Property<ScriptEditorCell>> properties = new HashMap<>();

  private InputRuleProvider ruleProvider = new InputRuleProvider();

  public static ScriptEditorRow createFromTestStep(TestStep testStep) {
    ScriptEditorRow row = new ScriptEditorRow();
    row.loadTestStep(testStep);
    return row;
  }

  private void loadTestStep(TestStep testStep) {
    setValue(noProperty(), testStep.getNo());
    setValue(itemNameProperty(), testStep.getItemName());
    setValue(operationNameProperty(), testStep.getOperationName());
    setValue(locatorTypeProperty(), testStep.getLocator().getType());
    setValue(locatorProperty(), testStep.getLocator().getValue());
    setValue(dataTypeProperty(), testStep.getDataType());
    setValue(screenshotTimingProperty(), testStep.getScreenshotTiming().getLabel());
    setValue(breakpointProperty(), testStep.getBreakPoint());
    for (Entry<String, String> entry : testStep.getTestData().entrySet()) {
      setValue(testDataProperty(entry.getKey()), entry.getValue());
    }
  }

  public TestStep buildTestStep(List<String> caseNoList) {
    TestStep testStep = new TestStep();
    testStep.setNo(noProperty().getValue().getValue());
    testStep.setItemName(itemNameProperty().getValue().getValue());
    testStep.setOperationName(operationNameProperty().getValue().getValue());
    Locator loc = new Locator();
    loc.setType(locatorTypeProperty().getValue().getValue());
    loc.setValue(locatorProperty().getValue().getValue());
    testStep.setLocator(loc);
    testStep.setDataType(dataTypeProperty().getValue().getValue());
    testStep.setScreenshotTiming(screenshotTimingProperty().getValue().getValue());
    testStep.setBreakPoint(breakpointProperty().getValue().getValue());

    Map<String, String> data = new LinkedHashMap<>();
    for (String caseNo : caseNoList) {
      data.put(caseNo, testDataProperty(caseNo).getValue().getValue());
    }
    testStep.setTestData(data);

    return testStep;
  }

  public void setDebugCase(String caseNo) {
    removeDebugCase();
    setDebugCase(testDataProperty(caseNo), true);
  }

  public void removeDebugCase() {
    properties.values().forEach(tdp -> setDebugCase(tdp, false));
  }

  public void setDebugStep() {
    properties.values().forEach(tdp -> setDebugStep(tdp, true));
  }

  public void removeDebugStep() {
    properties.values().forEach(tdp -> setDebugStep(tdp, false));
  }

  private void setValue(Property<ScriptEditorCell> p, String value) {
    if (p.getValue().getInputRule().match(value)) {
      p.setValue(p.getValue().toBuilder().value(value).build());
    }
  }

  private void setInputRule(Property<ScriptEditorCell> p, InputRule rule) {
    ScriptEditorCell oldCell = p.getValue();
    String newValue = rule.match(oldCell.getValue()) ? oldCell.getValue() : rule.defalutValue();
    p.setValue(oldCell.toBuilder().inputRule(rule).value(newValue).build());
  }

  private void setDebugStep(Property<ScriptEditorCell> p, boolean debugStep) {
    ScriptEditorCell cell = p.getValue();
    if (debugStep != cell.isDebugStep()) {
      p.setValue(cell.toBuilder().debugStep(debugStep).build());
    }
  }

  private void setDebugCase(Property<ScriptEditorCell> p, boolean debugCase) {
    ScriptEditorCell cell = p.getValue();
    if (debugCase != cell.isDebugCase()) {
      p.setValue(cell.toBuilder().debugCase(debugCase).build());
    }
  }

  public Property<ScriptEditorCell> noProperty() {
    ScriptEditorCell initial = createBlankCell(ruleProvider.getNoRule());
    return getProperty("no", initial);
  }

  public Property<ScriptEditorCell> itemNameProperty() {
    ScriptEditorCell initial = createBlankCell(ruleProvider.getItemNameRule());
    return getProperty("itemName", initial);
  }

  public Property<ScriptEditorCell> operationNameProperty() {
    ScriptEditorCell initial = createBlankCell(ruleProvider.getOperationNameRule());
    return properties.computeIfAbsent(
        "operationName",
        name -> {
          Property<ScriptEditorCell> p = new SimpleObjectProperty<>(this, name, initial);
          p.addListener(this::onOperationNameCellChanged);
          return p;
        });
  }

  private String getOperationNameValue() {
    return operationNameProperty().getValue().getValue();
  }

  public Property<ScriptEditorCell> locatorTypeProperty() {
    ScriptEditorCell initial =
        createBlankCell(ruleProvider.getLocatorTypeRule(getOperationNameValue()));
    return getProperty("locatorType", initial);
  }

  public Property<ScriptEditorCell> locatorProperty() {
    ScriptEditorCell initial =
        createBlankCell(ruleProvider.getLocatorRule(getOperationNameValue()));
    return getProperty("locator", initial);
  }

  public Property<ScriptEditorCell> dataTypeProperty() {
    ScriptEditorCell initial =
        createBlankCell(ruleProvider.getDataTypeRule(getOperationNameValue()));
    return getProperty("dataType", initial);
  }

  public Property<ScriptEditorCell> screenshotTimingProperty() {
    ScriptEditorCell initial = createBlankCell(ruleProvider.getScreenshotTimingRule());
    return getProperty("screenshotTiming", initial);
  }

  public Property<ScriptEditorCell> breakpointProperty() {
    ScriptEditorCell initial = createBlankCell(ruleProvider.getBreakpointRule());
    return getProperty("breakpoint", initial);
  }

  public Property<ScriptEditorCell> testDataProperty(String caseNo) {
    ScriptEditorCell initial =
        createBlankCell(ruleProvider.getTestDataRule(getOperationNameValue()));
    return getProperty(TEST_DATA_PROP_PREFIX + caseNo, initial);
  }

  private ScriptEditorCell createBlankCell(InputRule rule) {
    return ScriptEditorCell.builder().inputRule(rule).value(rule.defalutValue()).build();
  }

  private Property<ScriptEditorCell> getProperty(String name, ScriptEditorCell initial) {
    return properties.computeIfAbsent(name, n -> new SimpleObjectProperty<>(this, n, initial));
  }

  private void onOperationNameCellChanged(
      ObservableValue<? extends ScriptEditorCell> operationProperty,
      ScriptEditorCell oldOperationCell,
      ScriptEditorCell newOperationCell) {

    String newOperationName = newOperationCell.getValue();
    if (StringUtils.equals(oldOperationCell.getValue(), newOperationName)) {
      return;
    }
    setInputRule(locatorTypeProperty(), ruleProvider.getLocatorTypeRule(newOperationName));
    setInputRule(locatorProperty(), ruleProvider.getLocatorRule(newOperationName));
    setInputRule(dataTypeProperty(), ruleProvider.getDataTypeRule(newOperationName));
    for (String caseNo : getCaseNos()) {
      setInputRule(testDataProperty(caseNo), ruleProvider.getTestDataRule(newOperationName));
    }
  }

  private List<String> getCaseNos() {
    return properties
        .keySet()
        .stream()
        .map(name -> StringUtils.substringAfter(name, TEST_DATA_PROP_PREFIX))
        .filter(caseNo -> !caseNo.isEmpty())
        .collect(toList());
  }
}
