package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import io.sitoolkit.wt.domain.testscript.TestStepInputType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TestScriptInputHelper {

    private static final int COLUMN_INDEX_FIRST_CASE = 7;

    private static final int COL_INDEX_OPERATION = 2;
    private static final int COL_INDEX_LOCATOR_STYLE = 3;
    private static final int COL_INDEX_LOCATOR = 4;
    private static final int COL_INDEX_DATA_STYLE = 5;

    private static final SpreadsheetCellType<?> UNUSED_TYPE = new UnusedCellType();
    private static final SpreadsheetCellType<?> OK_CANCEL_DATA_TYPE = new OkCancelDataCellType();

    private SpreadsheetCellType<?> operationCellType;
    private SpreadsheetCellType<?> screenshotCellType;

    private SpreadsheetView spreadSheet;

    private TestScriptEditorCellBuilder cellBuilder = new TestScriptEditorCellBuilder();

    public TestScriptInputHelper(SpreadsheetView spreadSheet, TestStepInputType[] inputTypes,
            List<String> screenshotTimingValues) {
        this.spreadSheet = spreadSheet;
        List<String> operationNames = Arrays.asList(inputTypes).stream()
                .map(TestStepInputType::getOperationName).collect(Collectors.toList());
        operationCellType = new OperationCellType(operationNames, this::updateStepOperation);
        screenshotCellType = SpreadsheetCellType.LIST(screenshotTimingValues);
    }

    public SpreadsheetCell buildDefaultCell(int rowIndex, int colIndex, String value) {
        return cellBuilder.build(SpreadsheetCellType.STRING, rowIndex, colIndex, value);
    }

    public SpreadsheetCell buildOperationCell(int rowIndex, int colIndex, String value) {
        return cellBuilder.build(operationCellType, rowIndex, colIndex, value);
    }

    public SpreadsheetCell buildLocatorStyleCell(String operationName, int rowIndex, int colIndex,
            String value) {

        List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();

        SpreadsheetCellType<?> cellType;
        if (locatorTypes.size() <= 1) {
            cellType = UNUSED_TYPE;
        } else {
            cellType = SpreadsheetCellType.LIST(locatorTypes);
        }

        return cellBuilder.build(cellType, rowIndex, colIndex, value);
    }

    public SpreadsheetCell buildLocatorCell(String operationName, int rowIndex, int colIndex,
            String value) {

        List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();

        SpreadsheetCellType<?> cellType;
        if (locatorTypes.get(0).equals("na")) {
            cellType = UNUSED_TYPE;
        } else {
            cellType = SpreadsheetCellType.STRING;
        }

        return cellBuilder.build(cellType, rowIndex, colIndex, value);
    }

    public SpreadsheetCell buildDataTypeCell(String operationName, int rowIndex, int colIndex,
            String value) {

        List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();

        SpreadsheetCellType<?> cellType;
        if (dataTypes.size() <= 1) {
            cellType = UNUSED_TYPE;
        } else {
            cellType = SpreadsheetCellType.LIST(dataTypes);
        }

        return cellBuilder.build(cellType, rowIndex, colIndex, value);
    }

    public SpreadsheetCell buildScreenshotCell(int rowIndex, int colIndex, String value) {
        return cellBuilder.build(screenshotCellType, rowIndex, colIndex, value);
    }

    public SpreadsheetCell buildDataCell(String operationName, int rowIndex, int colIndex,
            String value) {

        List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();

        SpreadsheetCellType<?> cellType;
        switch (dataTypes.get(0)) {
            case "ok_cancel":
                cellType = OK_CANCEL_DATA_TYPE;
                break;
            default:
                cellType = SpreadsheetCellType.STRING;
                break;
        }

        return cellBuilder.build(cellType, rowIndex, colIndex, value);
    }

    public void updateStepOperation(int rowIndex, String operationName) {
        ObservableList<SpreadsheetCell> row = spreadSheet.getGrid().getRows().get(rowIndex);

        row.set(COL_INDEX_LOCATOR_STYLE,
                buildLocatorStyleCell(operationName, rowIndex, COL_INDEX_LOCATOR_STYLE, null));
        row.set(COL_INDEX_LOCATOR,
                buildLocatorCell(operationName, rowIndex, COL_INDEX_LOCATOR, null));
        row.set(COL_INDEX_DATA_STYLE,
                buildLocatorCell(operationName, rowIndex, COL_INDEX_DATA_STYLE, null));

        IntStream.range(COLUMN_INDEX_FIRST_CASE, row.size()).forEach((colIndex) -> {
            row.set(colIndex, buildDataCell(operationName, rowIndex, colIndex, null));
        });

        SpreadsheetUtils.forceRedraw(spreadSheet);
    }

    public ObservableList<SpreadsheetCell> buildEmptyRow(int rowIndex, int columnCount) {
        ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();
        IntStream.range(0, columnCount).forEachOrdered(colIndex -> {
            SpreadsheetCell cell;
            if (colIndex == COL_INDEX_OPERATION) {
                cell = buildOperationCell(rowIndex, colIndex, "");
            } else {
                cell = buildDefaultCell(rowIndex, colIndex, "");
            }
            cells.add(cell);
        });
        return cells;
    }

    public SpreadsheetCell buildEmptyDataCell(ObservableList<SpreadsheetCell> row, int rowIndex,
            int colIndex) {
        String operationName = row.get(COL_INDEX_OPERATION).getText();
        return buildDataCell(operationName, rowIndex, colIndex, "");
    }
}
