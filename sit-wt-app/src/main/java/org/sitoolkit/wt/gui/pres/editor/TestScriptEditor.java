package org.sitoolkit.wt.gui.pres.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestStep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
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
        spreadSheet.setShowColumnHeader(false);
        spreadSheet.setShowRowHeader(false);
        spreadSheet.setId(testScript.getScriptFile().getAbsolutePath());

        TestScriptEditorController controller = new TestScriptEditorController(this, spreadSheet);
        spreadSheet.getContextMenu().getItems().addAll(createMenuItems(controller));

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

    public void addTestCase(SpreadsheetView spreadSheet) {

        insertTestCase(spreadSheet,  getInsertColumnPosition(spreadSheet), null);
    }

    public void pasteCases(SpreadsheetView spreadSheet, List<List<String>> cases) {

        int columnPosition = getInsertColumnPosition(spreadSheet);
        List<List<String>> reversedCases = new ArrayList<List<String>>(cases);
        Collections.reverse(reversedCases);
        reversedCases.stream().forEachOrdered(testCase -> insertTestCase(spreadSheet, columnPosition, testCase));
    }

    public void addTestStep(SpreadsheetView spreadSheet) {

        insertTestStep(spreadSheet, getInsertRowPosition(spreadSheet), null);
    }

    public void pasteSteps(SpreadsheetView spreadSheet, List<List<String>> steps) {

        int rowPosition = getInsertRowPosition(spreadSheet);
        List<List<String>> reversedSteps = new ArrayList<List<String>>(steps);
        Collections.reverse(reversedSteps);
        reversedSteps.stream().forEachOrdered(testStep -> insertTestStep(spreadSheet, rowPosition, testStep));

    }

    public List<List<String>> getSelectedCases(SpreadsheetView spreadSheet) {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel().getSelectedCells();

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();

        List<List<String>> cases = selectedCells.stream()
                .map(cell -> cell.getColumn())
                .filter(col -> col >= COLUMN_INDEX_FIRST_CASE)
                .distinct()
                .sorted()
                .map(i -> rows.stream()
                        .map(row -> row.get(i).getText())
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return cases;
    }


    public List<List<String>> getSelectedSteps(SpreadsheetView spreadSheet) {
        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel().getSelectedCells();

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();

        List<List<String>> steps = selectedCells.stream()
                .map(cell -> cell.getRow())
                .filter(row -> row >= ROW_INDEX_FIRST_STEP)
                .distinct()
                .sorted()
                .map(i -> rows.get(i).stream()
                        .map(cell -> cell.getText())
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return steps;
    }

    private int getInsertColumnPosition(SpreadsheetView spreadSheet) {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel().getSelectedCells();

        Optional<Integer> selectedMax = selectedCells.stream().map(cell -> cell.getColumn()).distinct()
                .max(Comparator.naturalOrder());

        int columnPosition = selectedMax.orElse(spreadSheet.getGrid().getColumnCount() - 1) + 1;

        if (columnPosition < COLUMN_INDEX_FIRST_CASE) {
            columnPosition  = spreadSheet.getGrid().getColumnCount();
        }

        return columnPosition;
    }

    private int getInsertRowPosition(SpreadsheetView spreadSheet) {

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = spreadSheet.getSelectionModel().getSelectedCells();

        Optional<Integer> selectedMax = selectedCells.stream().map(cell -> cell.getRow()).distinct()
                .max(Comparator.naturalOrder());

        int rowPosition = selectedMax.orElse(spreadSheet.getGrid().getRowCount() - 1) + 1;
        if (rowPosition < ROW_INDEX_FIRST_STEP) {
            rowPosition  = spreadSheet.getGrid().getRowCount();
        }
        return rowPosition;
    }


    private void insertTestStep(SpreadsheetView spreadSheet, int rowPosition, List<String> values) {

        Grid grid = spreadSheet.getGrid();
        int columnCount = grid.getColumnCount();

        ObservableList<ObservableList<SpreadsheetCell>> rows = grid.getRows();
        ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();

        IntStream.range(0, columnCount).forEachOrdered(i -> {
            String value = "";
            if (values != null && i < values.size()) {
                value = values.get(i);
            }
            cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), i, 1, 1, value));
        });
        rows.add(rowPosition, cells);
        grid.setRows(recreateRows(rows));

    }

    private void insertTestCase(SpreadsheetView spreadSheet, int columnPosition, List<String> values) {

        ObservableList<ObservableList<SpreadsheetCell>> rows = spreadSheet.getGrid().getRows();
        IntStream.range(0, rows.size()).forEach(i -> {
            String value = "";
            if (values != null && i < values.size()) {
                value = values.get(i);
            }
            SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(i, columnPosition, 1, 1, value);
            rows.get(i).add(columnPosition, cell);
        });
        ObservableList<SpreadsheetColumn> columns = spreadSheet.getColumns();
        List<Double> widths = columns.stream().map(col -> col.getWidth()).collect(Collectors.toList());
        widths.add(columnPosition, widths.get(columnPosition - 1));

        Grid newGrid = new GridBase(10, 10);
        newGrid.setRows(recreateRows(rows));
        spreadSheet.setGrid(newGrid);

        IntStream.range(columnPosition, columns.size()).forEach(i -> columns.get(i).setPrefWidth(widths.get(i)));
        if(values != null && !values.isEmpty()) {
            SpreadsheetColumn col =columns.get(columnPosition);
            boolean isFixed = col.isFixed();
            col.setFixed(false);
            col.fitColumn();
            col.setFixed(isFixed);
        }
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

    private ObservableList<MenuItem> createMenuItems(TestScriptEditorController controller) {
        ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
        MenuItem item;
        Menu menu;

        menuItems.add(new SeparatorMenuItem());

        menu = new Menu("テストケース");
        item = new MenuItem("新規ケースの挿入");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> controller.newTestCase(e));
        menu.getItems().add(item);

        item = new MenuItem("ケースのコピー");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> controller.copyCase(e));
        menu.getItems().add(item);

        item = new MenuItem("ケースの挿入貼付け");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> controller.pasteCase(e));
        menu.getItems().add(item);
        menuItems.add(menu);


        menu = new Menu("テスト項目");
        item = new MenuItem("新規項目の挿入");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> controller.newTestStep(e));
        menu.getItems().add(item);

        item = new MenuItem("項目のコピー");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> controller.copyStep(e));
        menu.getItems().add(item);

        item = new MenuItem("項目の挿入貼付け");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> controller.pasteStep(e));
        menu.getItems().add(item);

        menuItems.add(menu);

        return menuItems;
    }

}
