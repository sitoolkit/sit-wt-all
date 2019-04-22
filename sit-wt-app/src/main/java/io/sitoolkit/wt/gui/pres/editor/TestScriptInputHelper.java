package io.sitoolkit.wt.gui.pres.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TestScriptInputHelper {

    private static final int COLUMN_INDEX_FIRST_CASE = 7;
    
    private static final int COL_INDEX_OPERATION = 2;
    private static final int COL_INDEX_LOCATOR_STYLE = 3;
    private static final int COL_INDEX_LOCATOR = 4;
    private static final int COL_INDEX_DATA_STYLE = 5;

    private SpreadsheetCellType<?> operationCellType;
    private SpreadsheetCellType<?> screenshotCellType;

    private SpreadsheetView spreadSheet;

    private TestScriptEditorCellBuilder cellBuilder = new TestScriptEditorCellBuilder();
    
    public TestScriptInputHelper(SpreadsheetView spreadSheet, List<String> operationNames,
            List<String> screenshotTimingValues) {
        this.spreadSheet = spreadSheet;
        operationCellType = new OperationCellType(includeBlank(operationNames),
                this::updateStepOperation);
        screenshotCellType = SpreadsheetCellType.LIST(screenshotTimingValues);
    }
    
    private List<String> includeBlank(List<String> values) {
        List<String> result = new ArrayList<>(values);
        result.add(0, "");
        return Collections.unmodifiableList(result);
    }
  
    public SpreadsheetCell buildDefaultCell(int rowIndex, int colIndex, String value) {
        return cellBuilder.build(SpreadsheetCellType.STRING, rowIndex, colIndex, value);
    }

    public SpreadsheetCell buildOperationCell(int rowIndex, int colIndex, String value) {
        SpreadsheetCell cell = cellBuilder.build(operationCellType, rowIndex, colIndex, value);
        return cell;
    }

    public SpreadsheetCell buildLocatorStyleCell(String operationName, int rowIndex, int colIndex,
            String value) {

        List<String> values;
        if (operationName == "click") {
            values = Arrays.asList("id", "name", "tag", "link", "css", "xpath");
        } else {
            values = Arrays.asList("id", "name", "tag", "css", "xpath");
        }

        return cellBuilder.build(SpreadsheetCellType.LIST(values), rowIndex, colIndex, value);
    }

    public SpreadsheetCell buildLocatorCell(String operationName, int rowIndex, int colIndex,
            String value) {
        SpreadsheetCell cell = cellBuilder.build(SpreadsheetCellType.STRING, rowIndex, colIndex,
                value);

        if (operationName.equals("click")) {
            cell.setEditable(false);
        }

        return cell;
    }

    public SpreadsheetCell buildDataTypeCell(String operationName, int rowIndex, int colIndex,
            String value) {
        SpreadsheetCellType<?> type;
        if (operationName.equals("click")) {
            type = SpreadsheetCellType.LIST(Arrays.asList("label", "value"));
        } else {
            type = SpreadsheetCellType.STRING;
        }

        SpreadsheetCell cell = cellBuilder.build(type, rowIndex, colIndex, value);

        if (!operationName.equals("click")) {
            cell.setEditable(false);
        }

        return cell;
    }

    public SpreadsheetCell buildScreenshotCell(int rowIndex, int colIndex, String value) {
        return cellBuilder.build(screenshotCellType, rowIndex, colIndex, value);
    }

    public SpreadsheetCell buildDataCell(String operationName, int rowIndex, int colIndex,
            String value) {
        SpreadsheetCellType<?> type;
        if (operationName.equals("click")) {
            type = SpreadsheetCellType.LIST(Arrays.asList("", "y"));
        } else {
            type = SpreadsheetCellType.STRING;
        }

        return cellBuilder.build(type, rowIndex, colIndex, value);
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
    
    public ObservableList<SpreadsheetCell> buildTestStepRow(int rowIndex, int columnCount) {
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
    
    public SpreadsheetCell buildTestDataCell( ObservableList<SpreadsheetCell> row, int rowIndex, int colIndex) {
        String operationName = row.get(COL_INDEX_OPERATION).getText();
        return buildDataCell(operationName, rowIndex, colIndex, "");
    }
}
