package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.Picker;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.control.spreadsheet.SpreadsheetViewSelectionModel;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestStep;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TablePosition;
import javafx.scene.input.ContextMenuEvent;
import lombok.Getter;

public class TestScriptEditorSpreadSheetImpl implements TestScriptEditor {

  private static final int COLUMN_INDEX_FIRST_CASE = 7;
  private static final int ROW_INDEX_FIRST_STEP = 1;

  private static final int BREAK_POINT_HEADER_INDEX = 7;

  @Getter private SpreadsheetView spreadSheet = new SpreadsheetView();

  @Getter private int lastContextMenuRequestedRowIndex = -1;

  private TestScriptInputHelper inputHelper = new TestScriptInputHelper(spreadSheet);

  private ScriptClipboardAccessor clipboardAccessor;

  public void load(TestScript testScript) {
    Collection<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

    ObservableList<SpreadsheetCell> headerCells = buildHeaderRow(testScript.getHeaders());
    rows.add(headerCells);
    rows.addAll(buildTestStepRows(testScript.getTestStepList()));

    initRowPickers(testScript.getTestStepList());

    Grid grid = new GridBase(rows.size(), headerCells.size());
    grid.setRows(rows);

    spreadSheet.setGrid(grid);
    spreadSheet.setShowColumnHeader(true);
    spreadSheet.setShowRowHeader(true);
    spreadSheet.setId(testScript.getScriptFile().getAbsolutePath());
    spreadSheet
        .getStylesheets()
        .add(getClass().getResource("/testScriptEditor.css").toExternalForm());
    spreadSheet.setOnContextMenuRequested(new ContextMenuEventHandler());
  }

  private ObservableList<SpreadsheetCell> buildHeaderRow(List<String> scriptHeaders) {
    ObservableList<SpreadsheetCell> headerCells = FXCollections.observableArrayList();

    List<String> headers = new ArrayList<>(scriptHeaders);
    headers.remove(BREAK_POINT_HEADER_INDEX);

    headers
        .stream()
        .forEach(
            (header) -> {
              int colIndex = headerCells.size();

              SpreadsheetCell cell;
              if (isCaseColumn(colIndex)) {
                cell = inputHelper.buildStringCell(0, colIndex, header);
              } else {
                cell = inputHelper.buildReadOnlyCell(0, colIndex, header);
              }

              headerCells.add(cell);
            });

    return headerCells;
  }

  private ObservableList<ObservableList<SpreadsheetCell>> buildTestStepRows(
      List<TestStep> testSteps) {

    ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

    testSteps.forEach(
        testStep -> {
          rows.add(inputHelper.buildTestStepRow(ROW_INDEX_FIRST_STEP + rows.size(), testStep));
        });

    return rows;
  }

  private void initRowPickers(List<TestStep> testSteps) {
    ObservableMap<Integer, Picker> pickers = spreadSheet.getRowPickers();

    IntStream.range(0, testSteps.size())
        .forEach(
            i -> {
              pickers.put(
                  ROW_INDEX_FIRST_STEP + i,
                  new TestStepRowPicker(testSteps.get(i).isBreakPointEnabled()));
            });
  }

  public TestScript buildTestScript() {
    TestScript testScript = new TestScript();
    testScript.setScriptFile(new File(spreadSheet.getId()));

    ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();

    List<String> headers =
        rows.get(0).stream().map(SpreadsheetCell::getText).collect(Collectors.toList());
    headers.stream().forEach(header -> testScript.addHeader(header));

    List<TestStep> testStepList = new ArrayList<TestStep>();

    IntStream.range(ROW_INDEX_FIRST_STEP, rows.size())
        .forEach(
            rowIndex -> {
              ObservableList<SpreadsheetCell> row = rows.get(rowIndex);

              TestStep testStep = new TestStep();
              testStep.setNo(row.get(0).getText());
              testStep.setItemName(row.get(1).getText());
              testStep.setOperationName(row.get(2).getText());
              Locator locator = new Locator();
              locator.setType(row.get(3).getText());
              locator.setValue(row.get(4).getText());
              testStep.setLocator(locator);
              testStep.setDataType(row.get(5).getText());
              testStep.setScreenshotTiming(row.get(6).getText());
              testStep.setBreakPoint(getBreakPointValue(rowIndex));

              Map<String, String> testData = new LinkedHashMap<String, String>();
              for (int idx = COLUMN_INDEX_FIRST_CASE; idx < row.size(); idx++) {
                String caseNo = getCaseNo(headers.get(idx), testScript.getCaseNoPrefix());
                testData.put(caseNo, row.get(idx).getText());
              }
              testStep.setTestData(testData);
              testStepList.add(testStep);
            });

    testScript.setTestStepList(testStepList);

    return testScript;
  }

  private String getCaseNo(String value, String prefix) {
    if (value.startsWith(prefix)) {
      return StringUtils.substringAfter(value, prefix);
    } else {
      return value;
    }
  }

  private String getBreakPointValue(int rowIndex) {
    boolean isBreakpointEnabled =
        ((TestStepRowPicker) spreadSheet.getRowPickers().get(rowIndex)).isBreakpointEnabled();
    return isBreakpointEnabled ? "y" : null;
  }

  public void appendTestCase() {
    appendTestCases(1);
  }

  public void appendTestCases(int count) {
    insertTestCases(spreadSheet.getGrid().getColumnCount(), count);
  }

  public void appendTestStep() {
    appendTestSteps(1);
  }

  public void appendTestSteps(int count) {
    insertTestSteps(spreadSheet.getGrid().getRowCount(), count);
  }

  public boolean insertTestCase() {
    return insertTestCases(getSelectedColumnCount());
  }

  public boolean insertTestCases(int count) {
    Optional<Integer> insertPosition = getInsertColumnPosition();
    insertPosition.ifPresent(colPosition -> insertTestCases(colPosition, count));
    return insertPosition.isPresent();
  }

  public boolean insertTestStep() {
    return insertTestSteps(getSelectedRowCount());
  }

  public boolean insertTestSteps(int count) {
    Optional<Integer> insertPosition = getInsertRowPosition();
    insertPosition.ifPresent(rowPosition -> insertTestSteps(rowPosition, count));
    return insertPosition.isPresent();
  }

  public void deleteTestCase() {
    Set<Integer> deleteColumns = getSelectedCase();

    spreadSheet.getSelectionModel().clearSelection();

    ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();
    ObservableList<SpreadsheetColumn> columns = spreadSheet.getColumns();
    List<Double> widths = getColumnWidths();

    rows.stream()
        .forEach(
            row -> {
              deleteColumns
                  .stream()
                  .sorted(Comparator.reverseOrder())
                  .mapToInt(Integer::intValue)
                  .forEachOrdered(row::remove);
            });
    deleteColumns
        .stream()
        .sorted(Comparator.reverseOrder())
        .mapToInt(Integer::intValue)
        .forEachOrdered(widths::remove);

    int newColumnCount = columns.size() - deleteColumns.size();

    resetGrid(rows, newColumnCount);

    deleteColumns
        .stream()
        .forEach(
            (columnIndex) -> {
              if (columnIndex < newColumnCount) {
                selectRange(0, columnIndex, rows.size(), columnIndex + 1);
              }
            });

    setColumnWidths(widths);
  }

  public void deleteTestStep() {
    Set<Integer> deleteRows = getSelectedStep();

    ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();
    deleteRows
        .stream()
        .sorted(Comparator.reverseOrder())
        .mapToInt(Integer::intValue)
        .forEachOrdered(rows::remove);

    deleteRowPickers(deleteRows);

    resetGrid(rows, spreadSheet.getColumns().size());
  }

  private void deleteRowPickers(Set<Integer> deleteRows) {
    ObservableMap<Integer, Picker> pickers = spreadSheet.getRowPickers();

    deleteRows.forEach(pickers::remove);

    List<Integer> oldKeys = pickers.keySet().stream().sorted().collect(Collectors.toList());
    IntStream.range(0, pickers.size())
        .forEach(
            (i) -> {
              int newIndex = ROW_INDEX_FIRST_STEP + i;
              pickers.put(newIndex, pickers.get(oldKeys.get(i)));
            });
  }

  public boolean isCaseInsertable() {
    return getInsertColumnPosition().isPresent();
  }

  public boolean isStepInsertable() {
    return getInsertRowPosition().isPresent();
  }

  public boolean isCaseSelected() {
    return !getSelectedCase().isEmpty();
  }

  public boolean isStepSelected() {
    return !getSelectedStep().isEmpty();
  }

  public int getCaseCount(List<GridChange> changeList) {
    int columnCount = getColumnCount(changeList);
    int rowCount = getRowCount(changeList);
    return spreadSheet.getGrid().getRowCount() == rowCount ? columnCount : 0;
  }

  public int getStepCount(List<GridChange> changeList) {
    int columnCount = getColumnCount(changeList);
    int rowCount = getRowCount(changeList);
    return spreadSheet.getGrid().getColumnCount() == columnCount ? rowCount : 0;
  }

  private int getColumnCount(List<GridChange> changeList) {
    Set<Integer> colSet =
        changeList.stream().map(change -> change.getColumn()).collect(Collectors.toSet());
    return countMinToMax(colSet);
  }

  private int getRowCount(List<GridChange> changeList) {
    Set<Integer> rowSet =
        changeList.stream().map(change -> change.getRow()).collect(Collectors.toSet());
    return countMinToMax(rowSet);
  }

  private int countMinToMax(Collection<Integer> values) {
    return values.stream().max(Comparator.naturalOrder()).get()
        - values.stream().min(Comparator.naturalOrder()).get()
        + 1;
  }

  private Optional<Integer> getInsertColumnPosition() {

    @SuppressWarnings("rawtypes")
    ObservableList<TablePosition> selectedCells =
        spreadSheet.getSelectionModel().getSelectedCells();

    Optional<Integer> selectedMin =
        selectedCells
            .stream()
            .map(TablePosition::getColumn)
            .distinct()
            .min(Comparator.naturalOrder());

    return selectedMin.filter(this::isCaseColumn);
  }

  private Optional<Integer> getInsertRowPosition() {

    @SuppressWarnings("rawtypes")
    ObservableList<TablePosition> selectedCells =
        spreadSheet.getSelectionModel().getSelectedCells();

    Optional<Integer> selectedMin =
        selectedCells.stream().map(TablePosition::getRow).distinct().min(Comparator.naturalOrder());

    return selectedMin.filter(this::isStepRow);
  }

  private int getSelectedRowCount() {

    @SuppressWarnings("rawtypes")
    ObservableList<TablePosition> selectedCells =
        spreadSheet.getSelectionModel().getSelectedCells();

    return (int) selectedCells.stream().map(TablePosition::getRow).distinct().count();
  }

  private int getSelectedColumnCount() {

    @SuppressWarnings("rawtypes")
    ObservableList<TablePosition> selectedCells =
        spreadSheet.getSelectionModel().getSelectedCells();

    return (int) selectedCells.stream().map(TablePosition::getColumn).distinct().count();
  }

  private Set<Integer> getSelectedCase() {

    @SuppressWarnings("rawtypes")
    ObservableList<TablePosition> selectedCells =
        spreadSheet.getSelectionModel().getSelectedCells();

    Map<Integer, Integer> map = new HashMap<>();
    selectedCells
        .stream()
        .forEach(
            cell -> {
              int col = cell.getColumn();
              Optional<Integer> count = Optional.ofNullable(map.get(col));
              map.put(col, count.orElse(0) + 1);
            });

    int gridRowCount = spreadSheet.getGrid().getRowCount();
    boolean onlyCaseSelected =
        map.entrySet()
            .stream()
            .allMatch(
                entity -> {
                  return isCaseColumn(entity.getKey()) && entity.getValue() == gridRowCount;
                });

    return onlyCaseSelected ? map.keySet() : Collections.emptySet();
  }

  private Set<Integer> getSelectedStep() {

    @SuppressWarnings("rawtypes")
    ObservableList<TablePosition> selectedCells =
        spreadSheet.getSelectionModel().getSelectedCells();

    Map<Integer, Integer> map = new HashMap<>();
    selectedCells
        .stream()
        .forEach(
            cell -> {
              int row = cell.getRow();
              Optional<Integer> count = Optional.ofNullable(map.get(row));
              map.put(row, count.orElse(0) + 1);
            });

    int gridColumnCount = spreadSheet.getGrid().getColumnCount();
    boolean onlyStepSelected =
        map.entrySet()
            .stream()
            .allMatch(
                entity -> {
                  return isStepRow(entity.getKey()) && entity.getValue() == gridColumnCount;
                });

    return onlyStepSelected ? map.keySet() : Collections.emptySet();
  }

  private boolean isCaseColumn(int columnPosition) {
    return columnPosition >= COLUMN_INDEX_FIRST_CASE;
  }

  private boolean isStepRow(int rowPosition) {
    return rowPosition >= ROW_INDEX_FIRST_STEP;
  }

  private Optional<Integer> getCaseColumnPosition(int caseIndex) {
    int columnCount = spreadSheet.getGrid().getColumnCount();
    int position = COLUMN_INDEX_FIRST_CASE + caseIndex;
    return Optional.of(position).filter(p -> isCaseColumn(p) && p < columnCount);
  }

  private Optional<Integer> getStepRowPosition(int stepIndex) {
    int rowCount = spreadSheet.getGrid().getRowCount();
    int position = ROW_INDEX_FIRST_STEP + stepIndex;
    return Optional.of(position).filter(p -> isStepRow(p) && p < rowCount);
  }

  private void insertTestSteps(int rowPosition, int rowCount) {

    Grid grid = spreadSheet.getGrid();
    int columnCount = grid.getColumnCount();

    ObservableList<ObservableList<SpreadsheetCell>> rows = grid.getRows();

    IntStream.range(0, rowCount)
        .forEachOrdered(
            i -> {
              rows.add(rowPosition, inputHelper.buildEmptyRow(i, columnCount));
            });

    resetGrid(rows, columnCount);

    insertRowPickers(rowPosition, rowCount);

    reselectRange(rowPosition, 0, rowPosition + rowCount, columnCount);
  }

  private void insertTestCases(int columnPosition, int columnCount) {

    ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();
    ObservableList<SpreadsheetColumn> columns = spreadSheet.getColumns();
    List<Double> widths = getColumnWidths();

    IntStream.range(columnPosition, columnPosition + columnCount)
        .forEachOrdered(
            col -> {
              rows.get(0).add(col, inputHelper.buildStringCell(0, col, ""));

              IntStream.range(ROW_INDEX_FIRST_STEP, rows.size())
                  .forEach(
                      row -> {
                        rows.get(row)
                            .add(col, inputHelper.buildEmptyDataCell(rows.get(row), row, col));
                      });
            });

    IntStream.range(columnPosition, columnPosition + columnCount)
        .forEachOrdered(
            j -> {
              widths.add(j, widths.get(j - 1));
            });

    resetGrid(rows, columns.size() + columnCount);

    setColumnWidths(widths);

    reselectRange(0, columnPosition, rows.size(), columnPosition + columnCount);
  }

  private List<Double> getColumnWidths() {
    return spreadSheet
        .getColumns()
        .stream()
        .map(col -> col.getWidth())
        .collect(Collectors.toList());
  }

  private void setColumnWidths(List<Double> widths) {
    ObservableList<SpreadsheetColumn> columns = spreadSheet.getColumns();
    IntStream.range(0, widths.size()).forEach(i -> columns.get(i).setPrefWidth(widths.get(i)));
  }

  private void insertRowPickers(int rowPosition, int rowCount) {
    ObservableMap<Integer, Picker> pickers = spreadSheet.getRowPickers();

    IntStream.range(rowPosition, ROW_INDEX_FIRST_STEP + pickers.size())
        .boxed()
        .sorted(Comparator.reverseOrder())
        .forEach(
            i -> {
              pickers.put(i + rowCount, pickers.get(i));
            });

    IntStream.range(0, rowCount)
        .forEach(
            i -> {
              pickers.put(rowPosition + i, new TestStepRowPicker(false));
            });
  }

  private void reselectRange(
      int minRowInclude, int minColInclude, int maxRowExclude, int maxColExclude) {

    spreadSheet.getSelectionModel().clearSelection();
    selectRange(minRowInclude, minColInclude, maxRowExclude, maxColExclude);
  }

  private void selectRange(
      int minRowInclude, int minColInclude, int maxRowExclude, int maxColExclude) {

    SpreadsheetViewSelectionModel selection = spreadSheet.getSelectionModel();
    selection.selectRange(
        minRowInclude,
        spreadSheet.getColumns().get(minColInclude),
        maxRowExclude - 1,
        spreadSheet.getColumns().get(maxColExclude - 1));
  }

  private void resetGrid(ObservableList<ObservableList<SpreadsheetCell>> rows, int columnCount) {
    Grid newGrid = new GridBase(rows.size(), columnCount);
    newGrid.setRows(recreateRows(rows));
    spreadSheet.setGrid(newGrid);
  }

  private ObservableList<ObservableList<SpreadsheetCell>> recreateRows(
      ObservableList<ObservableList<SpreadsheetCell>> original) {

    ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
    rows.addAll(
        IntStream.range(0, original.size())
            .mapToObj(row -> recreateRow(original.get(row), row))
            .collect(Collectors.toList()));
    return rows;
  }

  private ObservableList<SpreadsheetCell> recreateRow(
      ObservableList<SpreadsheetCell> original, int row) {

    ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();
    cells.addAll(
        IntStream.range(0, original.size())
            .mapToObj(
                column -> {
                  return inputHelper.rebuildCell(original.get(column), row, column);
                })
            .collect(Collectors.toList()));
    return cells;
  }

  private void setStyle(int stepIndex, int caseIndex, String stepStyle, String caseStyle) {

    ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();

    getStepRowPosition(stepIndex)
        .ifPresent(
            position -> {
              rows.get(position)
                  .stream()
                  .forEach(
                      cell -> {
                        cell.getStyleClass().add(stepStyle);
                      });
            });

    getCaseColumnPosition(caseIndex)
        .ifPresent(
            position -> {
              rows.stream()
                  .forEach(
                      row -> {
                        row.get(position).getStyleClass().add(caseStyle);
                      });
            });
  }

  public void setDebugStyle(int stepIndex, int caseIndex) {
    removeDebugStyle();
    setStyle(stepIndex, caseIndex, "debugStep", "debugCase");
  }

  public void removeDebugStyle() {
    spreadSheet
        .getGrid()
        .getRows()
        .stream()
        .flatMap(ObservableList::stream)
        .forEach(
            cell -> {
              cell.getStyleClass().remove("debugStep");
              cell.getStyleClass().remove("debugCase");
            });
  }

  public void pasteClipboard() {
    spreadSheet.pasteClipboard();
  }

  public ContextMenu getContextMenu() {
    return spreadSheet.getContextMenu();
  }

  public void toggleBreakpoint() {
    if (!spreadSheet.getRowPickers().containsKey(lastContextMenuRequestedRowIndex)) {
      return;
    }

    ((TestStepRowPicker) spreadSheet.getRowPickers().get(lastContextMenuRequestedRowIndex))
        .toggleBreakpoint();
  }

  private class TestStepRowPicker extends Picker {
    private static final String ENABLE_CLASS = "enabled";

    @Getter private boolean isBreakpointEnabled;

    public TestStepRowPicker(boolean isBreakpointEnabled) {
      super("test-step-picker");
      this.isBreakpointEnabled = isBreakpointEnabled;
      refreshStyleClasses();
    }

    public void toggleBreakpoint() {
      isBreakpointEnabled = !isBreakpointEnabled;
      refreshStyleClasses();
      SpreadsheetUtils.forceRedraw(spreadSheet);
    }

    private void refreshStyleClasses() {
      ObservableList<String> styles = getStyleClass();
      if (isBreakpointEnabled) {
        styles.add(ENABLE_CLASS);
      } else {
        styles.remove(ENABLE_CLASS);
      }
    }

    @Override
    public void onClick() {
      lastContextMenuRequestedRowIndex =
          spreadSheet
              .getRowPickers()
              .entrySet()
              .stream()
              .filter((entry) -> entry.getValue().equals(this))
              .map(Entry::getKey)
              .findFirst()
              .get();
    }
  }

  private class ContextMenuEventHandler implements EventHandler<ContextMenuEvent> {
    @Override
    public void handle(ContextMenuEvent event) {
      if (isClickedOnCell(event)) {
        lastContextMenuRequestedRowIndex = ((IndexedCell<?>) event.getTarget()).getIndex();
      } else if (isClickedOnCellText(event)) {
        lastContextMenuRequestedRowIndex =
            ((IndexedCell<?>) ((Node) event.getTarget()).getParent()).getIndex();
      }
    }

    private boolean isClickedOnCell(ContextMenuEvent event) {
      return event.getTarget() instanceof IndexedCell;
    }

    private boolean isClickedOnCellText(ContextMenuEvent event) {
      return ((Node) event.getTarget()).getParent() instanceof IndexedCell;
    }
  }

  @Override
  public ScriptClipboardAccessor getClipboardAccessor() {
    if (clipboardAccessor == null) {
      clipboardAccessor = new SpreadsheetClipboardAccessor(this);
    }

    return clipboardAccessor;
  }
}
