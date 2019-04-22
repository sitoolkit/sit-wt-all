package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.domain.testscript.TestStepInputType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TestScriptInputHelper {

    private static final int COLUMN_INDEX_FIRST_CASE = 7;

    private static final int COL_INDEX_OPERATION = 2;
    private static final int COL_INDEX_LOCATOR_TYPE = 3;
    private static final int COL_INDEX_LOCATOR = 4;
    private static final int COL_INDEX_DATA_TYPE = 5;
    private static final int COL_INDEX_SCREENSHOT = 6;

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

    public ObservableList<SpreadsheetCell> buildTestStepRow(int rowIndex, TestStep testStep) {
        ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();

        cells.add(buildDefaultCell(rowIndex, cells.size(), testStep.getNo()));
        cells.add(buildDefaultCell(rowIndex, cells.size(), testStep.getItemName()));
        String operationName = testStep.getOperationName();
        cells.add(buildOperationCell(rowIndex, operationName));
        cells.add(buildLocatorTypeCell(operationName, rowIndex, testStep.getLocator().getType()));
        cells.add(buildLocatorCell(operationName, rowIndex, testStep.getLocator().getValue()));
        cells.add(buildDataTypeCell(operationName, rowIndex, testStep.getDataType()));
        cells.add(buildScreenshotCell(rowIndex, testStep.getScreenshotTiming().getLabel()));

        testStep.getTestData().values().stream().forEach(testData -> {
            cells.add(buildDataCell(operationName, rowIndex, cells.size(), testData));
        });

        return cells;
    }

    private SpreadsheetCell buildDefaultCell(int rowIndex, int colIndex, String value) {
        return cellBuilder.build(SpreadsheetCellType.STRING, rowIndex, colIndex, value);
    }

    private SpreadsheetCell buildOperationCell(int rowIndex, String value) {
        return cellBuilder.build(operationCellType, rowIndex, COL_INDEX_OPERATION, value);
    }

    private SpreadsheetCell buildLocatorTypeCell(String operationName, int rowIndex, String value) {

        return cellBuilder.build(getLocatorTypeCellType(operationName), rowIndex,
                COL_INDEX_LOCATOR_TYPE, value);
    }

    private SpreadsheetCellType<?> getLocatorTypeCellType(String operationName) {
        List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
        if (locatorTypes.size() <= 1) {
            return UNUSED_TYPE;
        } else {
            return SpreadsheetCellType.LIST(locatorTypes);
        }
    }

    private SpreadsheetCell buildLocatorCell(String operationName, int rowIndex, String value) {

        return cellBuilder.build(getLocatorCellType(operationName), rowIndex, COL_INDEX_LOCATOR,
                value);
    }

    private SpreadsheetCellType<?> getLocatorCellType(String operationName) {
        List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
        if (locatorTypes.get(0).equals("na")) {
            return UNUSED_TYPE;
        } else {
            return SpreadsheetCellType.STRING;
        }
    }

    private SpreadsheetCell buildDataTypeCell(String operationName, int rowIndex, String value) {

        return cellBuilder.build(getDataTypeCellType(operationName), rowIndex, COL_INDEX_DATA_TYPE,
                value);
    }

    private SpreadsheetCellType<?> getDataTypeCellType(String operationName) {
        List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
        if (dataTypes.size() <= 1) {
            return UNUSED_TYPE;
        } else {
            return SpreadsheetCellType.LIST(dataTypes);
        }
    }

    private SpreadsheetCell buildScreenshotCell(int rowIndex, String value) {
        return cellBuilder.build(screenshotCellType, rowIndex, COL_INDEX_SCREENSHOT, value);
    }

    private SpreadsheetCell buildDataCell(String operationName, int rowIndex, int colIndex,
            String value) {

        return cellBuilder.build(getDataCellType(operationName), rowIndex, colIndex, value);
    }

    private SpreadsheetCellType<?> getDataCellType(String operationName) {
        List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
        switch (dataTypes.get(0)) {
            case "ok_cancel":
                return OK_CANCEL_DATA_TYPE;
            case "na":
                return UNUSED_TYPE;
            default:
                return SpreadsheetCellType.STRING;
        }
    }

    public void updateStepOperation(int rowIndex, String operationName) {
        ObservableList<SpreadsheetCell> cells = spreadSheet.getGrid().getRows().get(rowIndex);

        cells.set(COL_INDEX_LOCATOR_TYPE, buildLocatorTypeCell(operationName, rowIndex, null));
        cells.set(COL_INDEX_LOCATOR, buildLocatorCell(operationName, rowIndex, null));
        cells.set(COL_INDEX_DATA_TYPE, buildDataTypeCell(operationName, rowIndex, null));

        IntStream.range(COLUMN_INDEX_FIRST_CASE, cells.size()).forEach((colIndex) -> {
            cells.set(colIndex, buildDataCell(operationName, rowIndex, colIndex, null));
        });

        SpreadsheetUtils.forceRedraw(spreadSheet);
    }

    public ObservableList<SpreadsheetCell> buildEmptyRow(int rowIndex, int columnCount) {
        ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();
        String operationName = "";

        cells.add(buildDefaultCell(rowIndex, cells.size(), null));
        cells.add(buildDefaultCell(rowIndex, cells.size(), null));
        cells.add(buildOperationCell(rowIndex, operationName));
        cells.add(buildLocatorTypeCell(operationName, rowIndex, null));
        cells.add(buildLocatorCell(operationName, rowIndex, null));
        cells.add(buildDataTypeCell(operationName, rowIndex, null));
        cells.add(buildScreenshotCell(rowIndex, null));

        IntStream.range(COLUMN_INDEX_FIRST_CASE, columnCount).forEach(colIndex -> {
            cells.add(buildDataCell(operationName, rowIndex, cells.size(), null));
        });

        return cells;
    }

    public SpreadsheetCell buildEmptyDataCell(ObservableList<SpreadsheetCell> row, int rowIndex,
            int colIndex) {

        String operationName = row.get(COL_INDEX_OPERATION).getText();
        return buildDataCell(operationName, rowIndex, colIndex, null);
    }
}
