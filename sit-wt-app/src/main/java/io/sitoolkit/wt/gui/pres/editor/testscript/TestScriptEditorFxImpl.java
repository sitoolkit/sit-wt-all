package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.Arrays;
import java.util.List;
import org.controlsfx.control.spreadsheet.GridChange;
import io.sitoolkit.wt.domain.testscript.TestScript;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TestScriptEditorFxImpl implements TestScriptEditor {

  private TableView<ScriptEditorRow> tableView = new TableView<>();

  @Override
  public void load(TestScript testScript) {

    ObservableList<ScriptEditorRow> rows = buildEditorRows(testScript);
    tableView.setItems(rows);
    tableView.getColumns().setAll(getViewColumns());
  }

  private List<TableColumn<ScriptEditorRow, String>> getViewColumns() {
    TableColumn<ScriptEditorRow, String> firstNameCol = new TableColumn<>("Column-1");
    firstNameCol.setCellValueFactory(new PropertyValueFactory<>("column1"));
    TableColumn<ScriptEditorRow, String> lastNameCol = new TableColumn<>("Column-2");
    lastNameCol.setCellValueFactory(new PropertyValueFactory<>("column2"));
    return Arrays.asList(firstNameCol, lastNameCol);
  }

  private ObservableList<ScriptEditorRow> buildEditorRows(TestScript testScript) {
    ObservableList<ScriptEditorRow> rows = FXCollections.observableArrayList();
    ScriptEditorRow row1 = new ScriptEditorRow();
    row1.setColumn1("R1C1");
    row1.setColumn2("R1C2");
    ScriptEditorRow row2 = new ScriptEditorRow();
    row2.setColumn1("R2C1");
    row2.setColumn2("R2C2");
    rows.addAll(row1, row2);
    return rows;
  }

  @Override
  public ContextMenu getContextMenu() {
    // TODO Auto-generated method stub
    return new ContextMenu();
  }

  @Override
  public TestScript buildTestScript() {
    // TODO Auto-generated method stub
    return null;
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
