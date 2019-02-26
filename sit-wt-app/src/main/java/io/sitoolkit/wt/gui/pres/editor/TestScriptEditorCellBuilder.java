package io.sitoolkit.wt.gui.pres.editor;

import java.util.List;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

public class TestScriptEditorCellBuilder {

    public SpreadsheetCell buildStringCell(int rowIndex, int colIndex, String value) {
        return SpreadsheetCellType.STRING.createCell(rowIndex, colIndex, 1, 1, value);
    }

    public SpreadsheetCell buildListCell(int rowIndex, int colIndex, String value,
            List<String> list) {
        return SpreadsheetCellType.LIST(list).createCell(rowIndex, colIndex, 1, 1, value);
    }

}
