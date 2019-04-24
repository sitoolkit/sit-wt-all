package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.List;
import java.util.stream.IntStream;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import io.sitoolkit.wt.domain.testscript.ScreenshotTiming;
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

    private static final TestScriptCellType DISABLED_TYPE = new DisabledCellType();
    private static final TestScriptCellType OK_CANCEL_TYPE = new OkCancelCellType();
    private static final TestScriptCellType STRING_TYPE = new StringCellType();
    private static final TestScriptCellType SCREENSHOT_TYPE = new ListCellType(
            ScreenshotTiming.getLabels());

    private TestScriptCellType OPERATION_TYPE = new OperationCellType(this::updateStepOperation);

    private SpreadsheetView spreadSheet;

    public TestScriptInputHelper(SpreadsheetView spreadSheet) {
        this.spreadSheet = spreadSheet;
    }

    public ObservableList<SpreadsheetCell> buildTestStepRow(int rowIndex, TestStep testStep) {
        ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();

        cells.add(STRING_TYPE.createCell(rowIndex, cells.size(), testStep.getNo()));
        cells.add(STRING_TYPE.createCell(rowIndex, cells.size(), testStep.getItemName()));
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

    private SpreadsheetCell buildOperationCell(int rowIndex, String value) {
        return OPERATION_TYPE.createCell(rowIndex, COL_INDEX_OPERATION, value);
    }

    private SpreadsheetCell buildLocatorTypeCell(String operationName, int rowIndex, String value) {

        return getLocatorTypeCellType(operationName).createCell(rowIndex, COL_INDEX_LOCATOR_TYPE,
                value);
    }

    private TestScriptCellType getLocatorTypeCellType(String operationName) {
        List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
        if (locatorTypes.size() <= 1) {
            return DISABLED_TYPE;
        } else {
            return new ListCellType(locatorTypes);
        }
    }

    private SpreadsheetCell buildLocatorCell(String operationName, int rowIndex, String value) {

        return getLocatorCellType(operationName).createCell(rowIndex, COL_INDEX_LOCATOR, value);
    }

    private TestScriptCellType getLocatorCellType(String operationName) {
        List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
        if (locatorTypes.get(0).equals("")) {
            return DISABLED_TYPE;
        } else {
            return STRING_TYPE;
        }
    }

    private SpreadsheetCell buildDataTypeCell(String operationName, int rowIndex, String value) {

        return getDataTypeCellType(operationName).createCell(rowIndex, COL_INDEX_DATA_TYPE, value);
    }

    private TestScriptCellType getDataTypeCellType(String operationName) {
        List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
        if (dataTypes.size() <= 1) {
            return DISABLED_TYPE;
        } else {
            return new ListCellType(dataTypes);
        }
    }

    private SpreadsheetCell buildScreenshotCell(int rowIndex, String value) {
        return SCREENSHOT_TYPE.createCell(rowIndex, COL_INDEX_SCREENSHOT, value);
    }

    private SpreadsheetCell buildDataCell(String operationName, int rowIndex, int colIndex,
            String value) {

        return getDataCellType(operationName).createCell(rowIndex, colIndex, value);
    }

    private TestScriptCellType getDataCellType(String operationName) {
        List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
        switch (dataTypes.get(0)) {
            case "ok_cancel":
                return OK_CANCEL_TYPE;
            case "na":
                return DISABLED_TYPE;
            default:
                return STRING_TYPE;
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

        cells.add(STRING_TYPE.createCell(rowIndex, cells.size(), null));
        cells.add(STRING_TYPE.createCell(rowIndex, cells.size(), null));
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
