package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import io.sitoolkit.wt.domain.testscript.TestStep;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScriptEditorRow {

  private StringProperty no;
  private StringProperty itemName;
  private StringProperty operationName;
  private StringProperty locatorType;
  private StringProperty locator;
  private StringProperty dataType;
  private StringProperty screenshotTiming;
  private StringProperty breakpoint; // TODO move cell to row-selector

  // caseName -> property of testData
  private Map<String, StringProperty> testData = new HashMap<>();

  public static ScriptEditorRow createFromTestStep(TestStep testStep) {
    ScriptEditorRow row = new ScriptEditorRow();
    row.loadTestStep(testStep);
    return row;
  }

  private void loadTestStep(TestStep testStep) {
    noProperty().setValue(testStep.getNo());
    itemNameProperty().setValue(testStep.getItemName());
    operationNameProperty().setValue(testStep.getOperationName());
    locatorTypeProperty().setValue(testStep.getLocator().getType());
    locatorProperty().setValue(testStep.getLocator().getValue());
    dataTypeProperty().setValue(testStep.getDataType());
    screenshotTimingProperty().setValue(testStep.getScreenshotTiming().getLabel());
    breakpointProperty().setValue(testStep.getBreakPoint());

    for (Entry<String, String> entry : testStep.getTestData().entrySet()) {
      testDataProperty(entry.getKey()).setValue(entry.getValue());
    }
  }

  public TestStep buildTestStep() {
    return null;
  }

  public StringProperty noProperty() {
    if (no == null) no = new SimpleStringProperty(this, "no");
    return no;
  }

  public StringProperty itemNameProperty() {
    if (itemName == null) itemName = new SimpleStringProperty(this, "itemName");
    return itemName;
  }

  public StringProperty operationNameProperty() {
    if (operationName == null) operationName = new SimpleStringProperty(this, "operationName");
    return operationName;
  }

  public StringProperty locatorTypeProperty() {
    if (locatorType == null) locatorType = new SimpleStringProperty(this, "locatorType");
    return locatorType;
  }

  public StringProperty locatorProperty() {
    if (locator == null) locator = new SimpleStringProperty(this, "locator");
    return locator;
  }

  public StringProperty dataTypeProperty() {
    if (dataType == null) dataType = new SimpleStringProperty(this, "dataType");
    return dataType;
  }

  public StringProperty screenshotTimingProperty() {
    if (screenshotTiming == null)
      screenshotTiming = new SimpleStringProperty(this, "screenshotTiming");
    return screenshotTiming;
  }

  public StringProperty breakpointProperty() {
    if (breakpoint == null) breakpoint = new SimpleStringProperty(this, "breakpoint");
    return breakpoint;
  }

  public StringProperty testDataProperty(String caseNo) {
    if (testData.get(caseNo) == null) {
      testData.put(caseNo, new SimpleStringProperty(this, "case_" + caseNo));
    }
    return testData.get(caseNo);
  }
}
