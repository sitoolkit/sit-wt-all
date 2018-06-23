package org.sitoolkit.wt.gui.pres.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestStep;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TestScriptEditor {

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
}
