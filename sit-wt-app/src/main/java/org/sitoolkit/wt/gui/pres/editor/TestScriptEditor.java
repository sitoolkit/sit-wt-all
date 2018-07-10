package org.sitoolkit.wt.gui.pres.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
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
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestStep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TablePosition;

public class TestScriptEditor {

    private static final int COLUMN_INDEX_FIRST_CASE = 8;
    private static final int ROW_INDEX_FIRST_STEP = 1;

    public SpreadsheetView buildSpreadsheet(TestScript testScript) {

        Collection<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

        ObservableList<SpreadsheetCell> headerCells = FXCollections.observableArrayList();
        testScript.getHeaders().forEach(header -> {
            headerCells.add(SpreadsheetCellType.STRING.createCell(rows.size(), headerCells.size(),
                    1, 1, header));
        });
        rows.add(headerCells);

        testScript.getTestStepList().stream().forEach(testStep -> {
            ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();

            cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                    testStep.getNo()));
            cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                    testStep.getItemName()));
            cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                    testStep.getOperationName()));
            cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                    testStep.getLocator().getType()));
            cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                    testStep.getLocator().getValue()));
            cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                    testStep.getDataType()));
            cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                    testStep.getScreenshotTiming()));
            cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                    testStep.getBreakPoint()));

            testStep.getTestData().values().stream().forEach(testData -> {
                cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                        testData));
            });
            rows.add(cells);
        });

        Grid grid = new GridBase(10, 10);
        grid.setRows(rows);

        SpreadsheetView spreadSheet = new SpreadsheetView(grid);
        spreadSheet.setShowColumnHeader(true);
        spreadSheet.setShowRowHeader(true);
        spreadSheet.setId(testScript.getScriptFile().getAbsolutePath());

        new TestScriptEditorController(spreadSheet);

        return spreadSheet;
    }

    public TestScript buildTestscript(SpreadsheetView spreadSheet) {
        TestScript testScript = new TestScript();
        testScript.setScriptFile(new File(spreadSheet.getId()));

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();

        List<String> headers = rows.iterator().next().stream().map(SpreadsheetCell::getText)
                .collect(Collectors.toList());

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
            testStep.setBreakPoint(row.get(7).getText());

            Map<String, String> testData = new HashMap<String, String>();
            for (int idx = 8; idx < row.size(); idx++) {
                String caseNo = StringUtils.substringAfter(headers.get(idx),
                        testScript.getCaseNoPrefix());
                testData.put(caseNo, row.get(idx).getText());
            }
            testStep.setTestData(testData);
            testStepList.add(testStep);
        });

        testScript.setTestStepList(testStepList);
        return testScript;
    }

    public void appendTestCase(SpreadsheetView spreadSheet) {
        appendTestCases(spreadSheet, 1);
    }
    public void appendTestCases(SpreadsheetView spreadSheet, int count) {
        insertTestCases(spreadSheet, spreadSheet.getGrid().getColumnCount(), count);
    }
    public void appendTestStep(SpreadsheetView spreadSheet) {
        appendTestSteps(spreadSheet, 1);
    }
    public void appendTestSteps(SpreadsheetView spreadSheet, int count) {
        insertTestSteps(spreadSheet, spreadSheet.getGrid().getRowCount(), count);
    }
    public boolean insertTestCase(SpreadsheetView spreadSheet) {
        return insertTestCases(spreadSheet, getSelectedColumnCount(spreadSheet));
    }

    public boolean insertTestCases(SpreadsheetView spreadSheet, int count) {
        Optional<Integer> insertPosition = getInsertColumnPosition(spreadSheet);
        insertPosition.ifPresent(colPosition -> insertTestCases(spreadSheet, colPosition, count));
        return insertPosition.isPresent();
    }

    public boolean insertTestStep(SpreadsheetView spreadSheet) {
        return insertTestSteps(spreadSheet, getSelectedRowCount(spreadSheet));
    }

    public boolean insertTestSteps(SpreadsheetView spreadSheet, int count) {
        Optional<Integer> insertPosition = getInsertRowPosition(spreadSheet);
        insertPosition.ifPresent(rowPosition -> insertTestSteps(spreadSheet, rowPosition, count));
        return insertPosition.isPresent();

    }

    public boolean isCaseInsertable(SpreadsheetView spreadSheet) {
        return getInsertColumnPosition(spreadSheet).isPresent();
    }

    public boolean isStepInsertable(SpreadsheetView spreadSheet) {
        return getInsertRowPosition(spreadSheet).isPresent();
    }

    public int getCaseCount(SpreadsheetView spreadSheet, List<GridChange> changeList) {
        int columnCount = getColumnCount(changeList);
        int rowCount = getRowCount(changeList);
        return spreadSheet.getGrid().getRowCount() == rowCount ? columnCount : 0;
    }

    public int getStepCount(SpreadsheetView spreadSheet, List<GridChange> changeList) {
        int columnCount = getColumnCount(changeList);
        int rowCount = getRowCount(changeList);
        return spreadSheet.getGrid().getColumnCount() == columnCount ? rowCount : 0;
    }

    private int getColumnCount(List<GridChange> changeList) {
        Set<Integer> colSet = changeList.stream().map(change -> change.getColumn()).collect(Collectors.toSet());
        return countMinToMax(colSet);
    }

    private int getRowCount(List<GridChange> changeList) {
        Set<Integer> rowSet = changeList.stream().map(change -> change.getRow()).collect(Collectors.toSet());
        return countMinToMax(rowSet);
    }

    private int countMinToMax(Collection<Integer> values) {
        return values.stream().max(Comparator.naturalOrder()).get()
                - values.stream().min(Comparator.naturalOrder()).get() + 1;

    }

    private Optional<Integer> getInsertColumnPosition(SpreadsheetView spreadSheet) {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel().getSelectedCells();

        Optional<Integer> selectedMin = selectedCells.stream().map(cell -> cell.getColumn()).distinct()
                .min(Comparator.naturalOrder());

        return selectedMin.filter(i -> i >= COLUMN_INDEX_FIRST_CASE);
    }

    private Optional<Integer> getInsertRowPosition(SpreadsheetView spreadSheet) {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel().getSelectedCells();

        Optional<Integer> selectedMin = selectedCells.stream().map(cell -> cell.getRow()).distinct()
                .min(Comparator.naturalOrder());

        return selectedMin.filter(i -> i >= ROW_INDEX_FIRST_STEP);
    }

    private int getSelectedRowCount(SpreadsheetView spreadSheet) {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel().getSelectedCells();

        return (int) selectedCells.stream().map(cell -> cell.getRow()).distinct().count();
    }

    private int getSelectedColumnCount(SpreadsheetView spreadSheet) {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel().getSelectedCells();

        return (int) selectedCells.stream().map(cell -> cell.getColumn()).distinct().count();
    }

    private void insertTestSteps(SpreadsheetView spreadSheet, int rowPosition, int rowCount) {

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
                rowPosition + rowCount - 1, spreadSheet.getColumns().get(spreadSheet.getColumns().size() - 1));
    }

    private void insertTestCases(SpreadsheetView spreadSheet, int columnPosition, int columnCount) {

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();
        IntStream.range(0, rows.size()).forEach(i -> {
            IntStream.range(columnPosition, columnPosition + columnCount).forEachOrdered(j -> {
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(i, j, 1, 1, "");
                rows.get(i).add(j, cell);
            });
        });
        ObservableList<SpreadsheetColumn> columns = spreadSheet.getColumns();
        List<Double> widths = columns.stream().map(col -> col.getWidth()).collect(Collectors.toList());
        IntStream.range(columnPosition, columnPosition + columnCount).forEachOrdered(j -> {
            widths.add(j, widths.get(j - 1));
        });

        Grid newGrid = new GridBase(10, 10);
        newGrid.setRows(recreateRows(rows));
        spreadSheet.setGrid(newGrid);
        IntStream.range(columnPosition, columns.size()).forEach(i -> columns.get(i).setPrefWidth(widths.get(i)));

        SpreadsheetViewSelectionModel selection = spreadSheet.getSelectionModel();
        selection.clearSelection();
        selection.selectRange(0, spreadSheet.getColumns().get(columnPosition),
                spreadSheet.getGrid().getRowCount() - 1,
                spreadSheet.getColumns().get(columnPosition + columnCount - 1));
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

    private ObservableList<SpreadsheetCell> recreateRow(ObservableList<SpreadsheetCell> original, int row) {
        ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();
        cells.addAll(
                IntStream.range(0, original.size())
                        .mapToObj(column -> recreateCell(original.get(column), row, column))
                        .collect(Collectors.toList()));

        return cells;
    }

    private SpreadsheetCell recreateCell(SpreadsheetCell original, int row, int column) {
        SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, original.getRowSpan(),
                original.getColumnSpan(), original.getText());
        return cell;
    }



}
