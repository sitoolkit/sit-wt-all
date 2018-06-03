package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.util.Collection;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.gui.app.script.ScriptService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Setter;

public class EditorTabController implements FileOpenable {

    @Setter
    TabPane tabs;

    ScriptService scriptService = new ScriptService();

    public void initialize() {
        scriptService.initialize();
    }

    Grid buildSpreadSheet(TestScript testScript) {

        Collection<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

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

            testStep.getTestData().values().stream().forEach(testData -> {
                cells.add(SpreadsheetCellType.STRING.createCell(rows.size(), cells.size(), 1, 1,
                        testData));
            });
            rows.add(cells);

        });

        Grid grid = new GridBase(10, 10);
        grid.setRows(rows);

        return grid;
    }

    @Override
    public void open(File file) {
        TestScript testScript = scriptService.read(file);
        Tab tab1 = new Tab("TestScript");
        tab1.setContent(new SpreadsheetView(buildSpreadSheet(testScript)));
        tabs.getTabs().add(tab1);
    }

}
