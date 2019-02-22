package io.sitoolkit.wt.gui.pres.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.control.spreadsheet.SpreadsheetViewSelectionModel;

import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestStep;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TablePosition;
import lombok.Getter;

public class TestScriptEditor {

    private static final int COLUMN_INDEX_FIRST_CASE = 8;
    private static final int ROW_INDEX_FIRST_STEP = 1;

    private static final List<String> SCREENSHOT_TIMING_VALUES;
    static {
        SCREENSHOT_TIMING_VALUES = Arrays.asList("", "前", "後");
    }

    private List<String> operationNames;

    @Getter
    private SpreadsheetView spreadSheet = new SpreadsheetView();

    public void load(TestScript testScript, List<String> operationNames) {
        this.operationNames = operationNames;
        this.operationNames.add(0, "");

        Collection<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

        ObservableList<SpreadsheetCell> headerCells = FXCollections.observableArrayList();
        testScript.getHeaders().forEach(header -> {
            SpreadsheetCell headerCell = SpreadsheetCellType.STRING.createCell(rows.size(),
                    headerCells.size(), 1, 1, header);
            boolean editable = (headerCells.size() < COLUMN_INDEX_FIRST_CASE) ? false : true;
            headerCell.setEditable(editable);
            headerCells.add(headerCell);
        });
        rows.add(headerCells);

        testScript.getTestStepList().stream().forEach(testStep -> {
            rows.add(createStepRow(testStep, rows.size()));
        });

        Grid grid = new GridBase(10, 10);
        grid.setRows(rows);

        spreadSheet.setGrid(grid);
        spreadSheet.setShowColumnHeader(true);
        spreadSheet.setShowRowHeader(true);
        spreadSheet.setId(testScript.getScriptFile().getAbsolutePath());
        spreadSheet.getStylesheets()
                .add(getClass().getResource("/testScriptEditor.css").toExternalForm());

    }

    private ObservableList<SpreadsheetCell> createStepRow(TestStep testStep, int rowNum) {
        ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();

        cells.add(SpreadsheetCellType.STRING.createCell(rowNum, cells.size(), 1, 1,
                testStep.getNo()));
        cells.add(SpreadsheetCellType.STRING.createCell(rowNum, cells.size(), 1, 1,
                testStep.getItemName()));
        cells.add(SpreadsheetCellType.LIST(operationNames).createCell(rowNum, cells.size(),
                1, 1, testStep.getOperationName()));
        cells.add(SpreadsheetCellType.STRING.createCell(rowNum, cells.size(), 1, 1,
                testStep.getLocator().getType()));
        cells.add(SpreadsheetCellType.STRING.createCell(rowNum, cells.size(), 1, 1,
                testStep.getLocator().getValue()));
        cells.add(SpreadsheetCellType.STRING.createCell(rowNum, cells.size(), 1, 1,
                testStep.getDataType()));
        cells.add(SpreadsheetCellType.LIST(SCREENSHOT_TIMING_VALUES).createCell(rowNum, cells.size(),
                1, 1, testStep.getScreenshotTiming()));
        SpreadsheetCell breakPointCell = SpreadsheetCellType.STRING.createCell(rowNum, cells.size(), 1, 1,
                null);
        CheckBox breakPointCheck = new CheckBox();
        breakPointCheck.setSelected(StringUtils.isEmpty(testStep.getBreakPoint()) ? false : true);
        breakPointCell.setGraphic(breakPointCheck);
        breakPointCell.setEditable(false);
        cells.add(breakPointCell);

        testStep.getTestData().values().stream().forEach(testData -> {
            cells.add(SpreadsheetCellType.STRING.createCell(rowNum, cells.size(), 1, 1,
                    testData));
        });

        return cells;
    }

    public TestScript buildTestscript() {
        spreadSheet.requestFocus();

        TestScript testScript = new TestScript();
        testScript.setScriptFile(new File(spreadSheet.getId()));

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();

        List<String> headers = rows.iterator().next().stream().map(SpreadsheetCell::getText)
                .collect(Collectors.toList());
        headers.stream().forEach(header -> testScript.addHeader(header));

        List<TestStep> testStepList = new ArrayList<TestStep>();
        rows.stream().skip(1L).forEach(row -> {
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
            testStep.setBreakPoint(((CheckBox)row.get(7).getGraphic()).isSelected() ? "y" : "");

            Map<String, String> testData = new LinkedHashMap<String, String>();
            for (int idx = 8; idx < row.size(); idx++) {
                String caseNo = (headers.get(idx).startsWith(testScript.getCaseNoPrefix()))
                        ? StringUtils.substringAfter(headers.get(idx), testScript.getCaseNoPrefix())
                        : headers.get(idx);
                testData.put(caseNo, row.get(idx).getText());
            }
            testStep.setTestData(testData);
            testStepList.add(testStep);
        });

        testScript.setTestStepList(testStepList);

        return testScript;
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

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();
        ObservableList<SpreadsheetColumn> columns = spreadSheet.getColumns();
        List<Double> widths = columns.stream().map(SpreadsheetColumn::getWidth)
                .collect(Collectors.toCollection(LinkedList::new));

        rows.stream().forEach(row -> {
            deleteColumns.stream().sorted(Comparator.reverseOrder()).mapToInt(Integer::intValue)
                    .forEachOrdered(row::remove);
        });
        deleteColumns.stream().sorted(Comparator.reverseOrder()).mapToInt(Integer::intValue)
                .forEachOrdered(widths::remove);

        Grid newGrid = new GridBase(10, 10);
        newGrid.setRows(recreateRows(rows));
        spreadSheet.setGrid(newGrid);

        IntStream.range(0, widths.size()).forEach(i -> columns.get(i).setPrefWidth(widths.get(i)));
    }

    public void deleteTestStep() {
        Set<Integer> deleteRows = getSelectedStep();

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();
        deleteRows.stream().sorted(Comparator.reverseOrder()).mapToInt(Integer::intValue)
                .forEachOrdered(rows::remove);

        Grid newGrid = new GridBase(10, 10);
        newGrid.setRows(recreateRows(rows));
        spreadSheet.setGrid(newGrid);

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
        Set<Integer> colSet = changeList.stream().map(change -> change.getColumn())
                .collect(Collectors.toSet());
        return countMinToMax(colSet);
    }

    private int getRowCount(List<GridChange> changeList) {
        Set<Integer> rowSet = changeList.stream().map(change -> change.getRow())
                .collect(Collectors.toSet());
        return countMinToMax(rowSet);
    }

    private int countMinToMax(Collection<Integer> values) {
        return values.stream().max(Comparator.naturalOrder()).get()
                - values.stream().min(Comparator.naturalOrder()).get() + 1;

    }

    private Optional<Integer> getInsertColumnPosition() {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel()
                .getSelectedCells();

        Optional<Integer> selectedMin = selectedCells.stream().map(TablePosition::getColumn)
                .distinct().min(Comparator.naturalOrder());

        return selectedMin.filter(this::isCaseColumn);
    }

    private Optional<Integer> getInsertRowPosition() {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel()
                .getSelectedCells();

        Optional<Integer> selectedMin = selectedCells.stream().map(TablePosition::getRow).distinct()
                .min(Comparator.naturalOrder());

        return selectedMin.filter(this::isStepRow);
    }

    private int getSelectedRowCount() {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel()
                .getSelectedCells();

        return (int) selectedCells.stream().map(TablePosition::getRow).distinct().count();
    }

    private int getSelectedColumnCount() {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel()
                .getSelectedCells();

        return (int) selectedCells.stream().map(TablePosition::getColumn).distinct().count();
    }

    private Set<Integer> getSelectedCase() {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel()
                .getSelectedCells();

        Map<Integer, Integer> map = new HashMap<>();
        selectedCells.stream().forEach(cell -> {
            int col = cell.getColumn();
            Optional<Integer> count = Optional.ofNullable(map.get(col));
            map.put(col, count.orElse(0) + 1);
        });

        int gridRowCount = spreadSheet.getGrid().getRowCount();
        boolean onlyCaseSelected = map.entrySet().stream().allMatch(entity -> {
            return isCaseColumn(entity.getKey()) && entity.getValue() == gridRowCount;
        });

        return onlyCaseSelected ? map.keySet() : Collections.emptySet();

    }

    private Set<Integer> getSelectedStep() {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel()
                .getSelectedCells();

        Map<Integer, Integer> map = new HashMap<>();
        selectedCells.stream().forEach(cell -> {
            int row = cell.getRow();
            Optional<Integer> count = Optional.ofNullable(map.get(row));
            map.put(row, count.orElse(0) + 1);
        });

        int gridColumnCount = spreadSheet.getGrid().getColumnCount();
        boolean onlyStepSelected = map.entrySet().stream().allMatch(entity -> {
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

    private Optional<Integer> getColumnPosition(int caseIndex) {
        int columnCount = spreadSheet.getGrid().getColumnCount();
        int position = COLUMN_INDEX_FIRST_CASE + caseIndex;
        return Optional.of(position).filter(p -> p >= COLUMN_INDEX_FIRST_CASE && p < columnCount);
    }

    private Optional<Integer> getRowPosition(int stepIndex) {
        int rowCount = spreadSheet.getGrid().getRowCount();
        int position = ROW_INDEX_FIRST_STEP + stepIndex;
        return Optional.of(position).filter(p -> p >= ROW_INDEX_FIRST_STEP && p < rowCount);
    }

    private void insertTestSteps(int rowPosition, int rowCount) {

        Grid grid = spreadSheet.getGrid();
        int columnCount = grid.getColumnCount();

        ObservableList<ObservableList<SpreadsheetCell>> rows = grid.getRows();
        ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();

        IntStream.range(0, rowCount).forEachOrdered(i -> {
            IntStream.range(0, columnCount).forEachOrdered(j -> {
                cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), j, 1, 1, ""));
            });
            rows.add(rowPosition, cells);
        });
        grid.setRows(recreateRows(rows));

        SpreadsheetViewSelectionModel selection = spreadSheet.getSelectionModel();
        selection.clearSelection();
        selection.selectRange(rowPosition, spreadSheet.getColumns().get(0),
                rowPosition + rowCount - 1,
                spreadSheet.getColumns().get(spreadSheet.getColumns().size() - 1));
    }

    private void insertTestCases(int columnPosition, int columnCount) {

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();
        IntStream.range(0, rows.size()).forEach(i -> {
            IntStream.range(columnPosition, columnPosition + columnCount).forEachOrdered(j -> {
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(i, j, 1, 1, "");
                rows.get(i).add(j, cell);
            });
        });
        ObservableList<SpreadsheetColumn> columns = spreadSheet.getColumns();
        List<Double> widths = columns.stream().map(col -> col.getWidth())
                .collect(Collectors.toList());
        IntStream.range(columnPosition, columnPosition + columnCount).forEachOrdered(j -> {
            widths.add(j, widths.get(j - 1));
        });

        Grid newGrid = new GridBase(10, 10);
        newGrid.setRows(recreateRows(rows));
        spreadSheet.setGrid(newGrid);
        IntStream.range(columnPosition, columns.size())
                .forEach(i -> columns.get(i).setPrefWidth(widths.get(i)));

        SpreadsheetViewSelectionModel selection = spreadSheet.getSelectionModel();
        selection.clearSelection();
        selection.selectRange(0, spreadSheet.getColumns().get(columnPosition),
                spreadSheet.getGrid().getRowCount() - 1,
                spreadSheet.getColumns().get(columnPosition + columnCount - 1));
    }

    private ObservableList<ObservableList<SpreadsheetCell>> recreateRows(
            ObservableList<ObservableList<SpreadsheetCell>> original) {
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        rows.addAll(IntStream.range(0, original.size())
                .mapToObj(row -> recreateRow(original.get(row), row)).collect(Collectors.toList()));
        return rows;

    }

    private ObservableList<SpreadsheetCell> recreateRow(ObservableList<SpreadsheetCell> original,
            int row) {
        ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();
        cells.addAll(IntStream.range(0, original.size())
                .mapToObj(column -> recreateCell(original.get(column), row, column))
                .collect(Collectors.toList()));

        return cells;
    }

    private SpreadsheetCell recreateCell(SpreadsheetCell original, int row, int column) {
        SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column,
                original.getRowSpan(), original.getColumnSpan(), original.getText());
        boolean editable = (row == 0 && column < COLUMN_INDEX_FIRST_CASE) ? false : true;
        cell.setEditable(editable);
        return cell;
    }

    private void setStyle(int stepIndex, int caseIndex, String stepStyle, String caseStyle) {

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();

        getRowPosition(stepIndex).ifPresent(position -> {
            rows.get(position).stream().forEach(cell -> {
                cell.getStyleClass().add(stepStyle);
            });
        });

        getColumnPosition(caseIndex).ifPresent(position -> {
            rows.stream().forEach(row -> {
                row.get(position).getStyleClass().add(caseStyle);
            });
        });
    }

    public void setDebugStyle(int stepIndex, int caseIndex) {
        removeDebugStyle();
        setStyle(stepIndex, caseIndex, "debugStep", "debugCase");
    }

    public void removeDebugStyle() {
        spreadSheet.getGrid().getRows().stream().flatMap(ObservableList::stream).forEach(cell -> {
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

}
