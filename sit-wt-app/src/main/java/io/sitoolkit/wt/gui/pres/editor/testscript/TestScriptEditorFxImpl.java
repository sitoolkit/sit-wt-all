package io.sitoolkit.wt.gui.pres.editor.testscript;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.spreadsheet.GridChange;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestStep;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class TestScriptEditorFxImpl implements TestScriptEditor {

  private static final int COLUMN_INDEX_FIRST_CASE = 8;

  private TableView<ScriptEditorRow> tableView = new TableView<>();

  @Override
  public void load(TestScript testScript) {
    tableView.setItems(buildEditorRows(testScript));
    tableView.getColumns().setAll(buildEditorColumns(testScript));
    tableView.setEditable(true);
    tableView.getSelectionModel().setCellSelectionEnabled(true);

    tableView.setId(testScript.getScriptFile().getAbsolutePath());
    tableView
        .getStylesheets()
        .add(getClass().getResource("/testScriptEditor.css").toExternalForm());

    // TODO implement SpreadSheet's RowPickers-like feature instead of breakpoint column
    // TODO set context-menu-event-handler to tableView
    //    tableView.setOnContextMenuRequested(new ContextMenuEventHandler());
  }

  private List<TableColumn<ScriptEditorRow, String>> buildEditorColumns(TestScript testScript) {
    List<TableColumn<ScriptEditorRow, String>> columns = new ArrayList<>();
    int colIndex = 0;
    for (String headerName : testScript.getHeaders()) {
      String caseNo = getCaseNo(testScript, headerName);
      columns.add(buildEditorColumn(headerName, colIndex, caseNo));
      colIndex++;
    }
    return columns;
  }

  private TableColumn<ScriptEditorRow, String> buildEditorColumn(
      String headerName, int columnIndex, String caseNo) {
    TableColumn<ScriptEditorRow, String> col = new TableColumn<>(headerName);
    col.setCellFactory(TextFieldTableCell.forTableColumn());
    col.setCellValueFactory(createCellValueFactory(columnIndex, caseNo));
    col.setEditable(true);
    return col;
  }

  private String getCaseNo(TestScript testScript, String headerName) {
    return StringUtils.substringAfter(headerName, testScript.getCaseNoPrefix());
  }

  private Callback<CellDataFeatures<ScriptEditorRow, String>, ObservableValue<String>>
      createCellValueFactory(int columnIndex, String caseNo) {

    switch (columnIndex) {
      case 0:
        return p -> p.getValue().noProperty();
      case 1:
        return p -> p.getValue().itemNameProperty();
      case 2:
        return p -> p.getValue().operationNameProperty();
      case 3:
        return p -> p.getValue().locatorTypeProperty();
      case 4:
        return p -> p.getValue().locatorProperty();
      case 5:
        return p -> p.getValue().dataTypeProperty();
      case 6:
        return p -> p.getValue().screenshotTimingProperty();
      case 7:
        return p -> p.getValue().breakpointProperty();
      default:
        return p -> p.getValue().testDataProperty(caseNo);
    }
  }

  private ObservableList<ScriptEditorRow> buildEditorRows(TestScript testScript) {
    return testScript
        .getTestStepList()
        .stream()
        .map(ScriptEditorRow::createFromTestStep)
        .collect(toCollection(FXCollections::observableArrayList));
  }

  @Override
  public ContextMenu getContextMenu() {
    if (tableView.getContextMenu() == null) {
      tableView.setContextMenu(new ContextMenu());
    }
    return tableView.getContextMenu();
  }

  @Override
  public TestScript buildTestScript() {
    TestScript testScript = new TestScript();
    testScript.setScriptFile(new File(tableView.getId()));
    List<String> headers =
        tableView.getColumns().stream().map(TableColumn::getText).collect(toList());
    List<String> caseNoList =
        headers
            .stream()
            .skip(COLUMN_INDEX_FIRST_CASE)
            .map(header -> getCaseNo(testScript, header))
            .collect(toList());
    List<TestStep> testStepList =
        tableView.getItems().stream().map(row -> row.buildTestStep(caseNoList)).collect(toList());

    headers.stream().forEach(header -> testScript.addHeader(header));
    testScript.setTestStepList(testStepList);
    return testScript;
  }

  @Override
  public void setDebugStyle(int nextStepIndex, int caseIndex) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeDebugStyle() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean insertTestCase() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void appendTestCase() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean insertTestStep() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void appendTestStep() {
    // TODO Auto-generated method stub

  }

  @Override
  public int getCaseCount(List<GridChange> changeList) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean insertTestCases(int count) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void pasteClipboard() {
    // TODO Auto-generated method stub

  }

  @Override
  public int getStepCount(List<GridChange> changeList) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean insertTestSteps(int count) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void appendTestCases(int count) {
    // TODO Auto-generated method stub

  }

  @Override
  public void appendTestSteps(int count) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteTestCase() {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteTestStep() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isCaseSelected() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isStepSelected() {
    tableView.getSelectionModel().getSelectedItem();
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isCaseInsertable() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isStepInsertable() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void toggleBreakpoint() {
    // TODO Auto-generated method stub

  }

  @Override
  public Node getSpreadSheet() {
    return tableView;
  }
}
