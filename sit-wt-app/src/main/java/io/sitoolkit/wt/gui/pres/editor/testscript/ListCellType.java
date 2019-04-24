package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.List;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

public class ListCellType extends SpreadsheetCellType.ListType implements TestScriptCellType {

    public ListCellType(List<String> items) {
        super(items);
    }

    @Override
    public SpreadsheetCell createCell(int row, int column, String value) {
        return super.createCell(row, column, ROW_SPAN, COL_SPAN, value);
    }
    
}
