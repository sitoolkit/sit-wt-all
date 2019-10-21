package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestStep;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class ScriptEditorRow {

  // propertyName -> property
  private Map<String, Property<ScriptEditorCell>> properties = new HashMap<>();

  public static ScriptEditorRow createFromTestStep(TestStep testStep) {
    ScriptEditorRow row = new ScriptEditorRow();
    row.loadTestStep(testStep);
    return row;
  }

  private void loadTestStep(TestStep testStep) {
    noProperty().setValue(ScriptEditorCell.of(testStep.getNo()));
    itemNameProperty().setValue(ScriptEditorCell.of(testStep.getItemName()));
    operationNameProperty().setValue(ScriptEditorCell.of(testStep.getOperationName()));
    locatorTypeProperty().setValue(ScriptEditorCell.of(testStep.getLocator().getType()));
    locatorProperty().setValue(ScriptEditorCell.of(testStep.getLocator().getValue()));
    dataTypeProperty().setValue(ScriptEditorCell.of(testStep.getDataType()));
    screenshotTimingProperty()
        .setValue(ScriptEditorCell.of(testStep.getScreenshotTiming().getLabel()));
    breakpointProperty().setValue(ScriptEditorCell.of(testStep.getBreakPoint()));

    for (Entry<String, String> entry : testStep.getTestData().entrySet()) {
      testDataProperty(entry.getKey()).setValue(ScriptEditorCell.of(entry.getValue()));
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
    return getProperty("no");
  }

  public Property<ScriptEditorCell> itemNameProperty() {
    return getProperty("itemName");
  }

  public Property<ScriptEditorCell> operationNameProperty() {
    return getProperty("operationName");
  }

  public Property<ScriptEditorCell> locatorTypeProperty() {
    return getProperty("locatorType");
  }

  public Property<ScriptEditorCell> locatorProperty() {
    return getProperty("locator");
  }

  public Property<ScriptEditorCell> dataTypeProperty() {
    return getProperty("dataType");
  }

  public Property<ScriptEditorCell> screenshotTimingProperty() {
    return getProperty("screenshotTiming");
  }

  public Property<ScriptEditorCell> breakpointProperty() {
    return getProperty("breakpoint");
  }

  public Property<ScriptEditorCell> testDataProperty(String caseNo) {
    return getProperty("case_" + caseNo);
  }

  private Property<ScriptEditorCell> getProperty(String name) {
    properties.computeIfAbsent(
        name, n -> new SimpleObjectProperty<>(this, n, ScriptEditorCell.of("")));
    return properties.get(name);
  }
}
