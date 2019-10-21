package io.sitoolkit.wt.gui.pres.editor.testscript;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestStep;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.util.Callback;

public class TestScriptEditorFxImpl implements TestScriptEditor {

  private static final int COLUMN_INDEX_FIRST_CASE = 8;

  private TableView<ScriptEditorRow> tableView = new TableView<>();

  private ClipboardScriptAccessor clipboardAccessor;

  private String caseNoPrefix = "case_";

  public static final KeyCodeCombination KEY_CODE_COPY =
      new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
  public static final KeyCodeCombination KEY_CODE_PASTE =
      new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);

  @Override
  public void load(TestScript testScript) {
    caseNoPrefix = testScript.getCaseNoPrefix();
    tableView.setItems(buildEditorRows(testScript));
    tableView.getColumns().setAll(buildEditorColumns(testScript));
    tableView.setEditable(true);
    tableView.getSelectionModel().setCellSelectionEnabled(true);
    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    tableView.setId(testScript.getScriptFile().getAbsolutePath());
    tableView
        .getStylesheets()
        .add(getClass().getResource("/testScriptEditor.css").toExternalForm());

    tableView.setOnKeyPressed(
        event -> {
          if (KEY_CODE_COPY.match(event)) {
            getClipboardAccessor().copy();
          }
          if (KEY_CODE_PASTE.match(event)) {
            getClipboardAccessor().paste();
          }
        });

    // TODO implement SpreadSheet's RowPickers-like feature instead of breakpoint column
    // TODO set context-menu-event-handler to tableView
    //    tableView.setOnContextMenuRequested(new ContextMenuEventHandler());
  }

  private List<TableColumn<ScriptEditorRow, ScriptEditorCell>> buildEditorColumns(
      TestScript testScript) {
    List<TableColumn<ScriptEditorRow, ScriptEditorCell>> columns = new ArrayList<>();
    int colIndex = 0;
    for (String headerName : testScript.getHeaders()) {
      String caseNo = getCaseNo(testScript, headerName);
      columns.add(buildEditorColumn(headerName, colIndex, caseNo));
      colIndex++;
    }
    return columns;
  }

  private TableColumn<ScriptEditorRow, ScriptEditorCell> buildEditorColumn(
      String headerName, int columnIndex, String caseNo) {
    TableColumn<ScriptEditorRow, ScriptEditorCell> col = new TableColumn<>(headerName);
    col.setCellFactory(l -> new ScriptEditorTestDataTableCell());
    col.setCellValueFactory(createCellValueFactory(columnIndex, caseNo));
    col.setEditable(true);
    col.setSortable(false);
    col.setReorderable(false);
    return col;
  }

  private String getCaseNo(TestScript testScript, String headerName) {
    return StringUtils.substringAfter(headerName, testScript.getCaseNoPrefix());
  }

  private String getCaseNo(String headerName) {
    return StringUtils.substringAfter(headerName, caseNoPrefix);
  }

  private String getCaseNo(int caseIndex) {
    return getCaseNo(tableView.getColumns().get(COLUMN_INDEX_FIRST_CASE + caseIndex).getText());
  }

  private String getCaseHeaderName(String caseNo) {
    return caseNoPrefix + caseNo;
  }

  private Callback<
          CellDataFeatures<ScriptEditorRow, ScriptEditorCell>, ObservableValue<ScriptEditorCell>>
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
    removeDebugStyle();
    tableView.getItems().get(nextStepIndex).setDebugStep();
    tableView.getItems().forEach(row -> row.setDebugCase(getCaseNo(caseIndex)));
  }

  @Override
  public void removeDebugStyle() {
    tableView.getItems().forEach(ScriptEditorRow::removeDebugCase);
    tableView.getItems().forEach(ScriptEditorRow::removeDebugStep);
  }

  @Override
  public boolean insertTestCase() {
    return insertTestCases(getSelectedCaseCount());
  }

  @Override
  public boolean insertTestCases(int count) {
    Optional<Integer> insertPosition = getInsertCasePosition();
    insertPosition.ifPresent(colPosition -> insertTestCases(colPosition, count));
    return insertPosition.isPresent();
  }

  private void insertTestCases(int colPosition, int count) {
    ObservableList<TableColumn<ScriptEditorRow, ?>> columns = tableView.getColumns();
    for (int i = 0; i < count; i++) {
      String caseNo = String.format("%04d", columns.size() - COLUMN_INDEX_FIRST_CASE + 1);
      columns.add(
          colPosition + i, buildEditorColumn(getCaseHeaderName(caseNo), colPosition + i, caseNo));
    }
    getSelection().clearAndSelect(0, tableView.getColumns().get(colPosition));
  }

  @Override
  public void appendTestCase() {
    appendTestCases(1);
  }

  @Override
  public void appendTestCases(int count) {
    insertTestCases(tableView.getColumns().size(), count);
  }

  @Override
  public void deleteTestCase() {
    getSelectedCaseIndexes()
        .stream()
        .sorted(reverseOrder())
        .mapToInt(Integer::intValue)
        .forEach(tableView.getColumns()::remove);
  }

  @Override
  public boolean insertTestStep() {
    return insertTestSteps(getSelectedRowCount());
  }

  @Override
  public boolean insertTestSteps(int count) {
    Optional<Integer> insertPosition = getInsertRowPosition();
    insertPosition.ifPresent(rowPosition -> insertTestSteps(rowPosition, count));
    return insertPosition.isPresent();
  }

  private void insertTestSteps(int rowPosition, int count) {
    ObservableList<ScriptEditorRow> rows = tableView.getItems();
    for (int i = 0; i < count; i++) {
      rows.add(rowPosition, new ScriptEditorRow());
    }
    getSelection().clearAndSelect(rowPosition, tableView.getColumns().get(0));
  }

  @Override
  public void appendTestStep() {
    appendTestSteps(1);
  }

  @Override
  public void appendTestSteps(int count) {
    insertTestSteps(tableView.getItems().size(), count);
  }

  @Override
  public void deleteTestStep() {
    tableView.getItems().removeAll(getSelection().getSelectedItems());
    getSelection().clearSelection();
  }

  @Override
  public boolean isCellSelected() {
    return getSelection().getSelectedItem() != null;
  }

  @Override
  public boolean isCaseSelected() {
    return !getSelectedCaseIndexes().isEmpty();
  }

  @Override
  public boolean isStepSelected() {
    return getSelection().getSelectedItem() != null;
  }

  @Override
  public boolean isCaseInsertable() {
    return getInsertCasePosition().isPresent();
  }

  @Override
  public boolean isStepInsertable() {
    return getInsertRowPosition().isPresent();
  }

  @Override
  public void toggleBreakpoint() {
    // TODO Auto-generated method stub

  }

  @Override
  public Node getSpreadSheet() {
    return tableView;
  }

  TableViewSelectionModel<ScriptEditorRow> getSelection() {
    return tableView.getSelectionModel();
  }

  private int getSelectedRowCount() {
    return (int) getSelection().getSelectedIndices().stream().distinct().count();
  }

  private int getSelectedCaseCount() {
    return (int) getSelectedCaseIndexes().stream().distinct().count();
  }

  private Optional<Integer> getInsertRowPosition() {
    return getSelection().getSelectedIndices().stream().min(Comparator.naturalOrder());
  }

  private Optional<Integer> getInsertCasePosition() {
    return getSelectedCaseIndexes().stream().min(Comparator.naturalOrder());
  }

  private Set<Integer> getSelectedCaseIndexes() {
    Set<Integer> selectedColumnIndexes =
        getSelection()
            .getSelectedCells()
            .stream()
            .map(TablePosition::getColumn)
            .distinct()
            .collect(toSet());

    boolean onlyCaseSelected = selectedColumnIndexes.stream().allMatch(this::isCaseColumn);
    return onlyCaseSelected ? selectedColumnIndexes : Collections.emptySet();
  }

  private boolean isCaseColumn(int columnPosition) {
    return columnPosition >= COLUMN_INDEX_FIRST_CASE;
  }

  @Override
  public ClipboardScriptAccessor getClipboardAccessor() {
    if (clipboardAccessor == null) {
      clipboardAccessor = new ClipboardScriptAccessorFxImpl(this);
    }
    return clipboardAccessor;
  }

  public String getCellValue(int row, int column) {
    if (isInRange(row, column)) {
      return getProperty(row, column).getValue().getValue();
    } else {
      return null;
    }
  }

  public void setCellValue(int row, int column, String value) {
    if (isInRange(row, column)) {
      Property<ScriptEditorCell> cellProperty = getProperty(row, column);
      ScriptEditorCell oldCell = cellProperty.getValue();
      cellProperty.setValue(
          new ScriptEditorCell(value, oldCell.isDebugCase(), oldCell.isDebugStep()));
    }
  }

  private boolean isInRange(int row, int column) {
    return 0 <= row
        && row < tableView.getItems().size()
        && 0 <= column
        && column < tableView.getColumns().size();
  }

  @SuppressWarnings("unchecked")
  private Property<ScriptEditorCell> getProperty(int row, int column) {
    return (Property<ScriptEditorCell>)
        tableView.getColumns().get(column).getCellObservableValue(row);
  }

  public int getRowCount() {
    return tableView.getItems().size();
  }

  public int getColumnCount() {
    return tableView.getColumns().size();
  }

  static class ScriptEditorTestDataTableCell
      extends TextFieldTableCell<ScriptEditorRow, ScriptEditorCell> {

    public ScriptEditorTestDataTableCell() {
      super(ScriptEditorCell.converter);
    }

    @Override
    public void updateItem(ScriptEditorCell cell, boolean empty) {
      super.updateItem(cell, empty);

      if (!empty) {
        getStyleClass().remove("debugCase");
        getStyleClass().remove("debugStep");

        if (cell.isDebugCase()) {
          getStyleClass().add("debugCase");
        }

        if (cell.isDebugStep()) {
          getStyleClass().add("debugStep");
        }
      }
    }
  }
}
