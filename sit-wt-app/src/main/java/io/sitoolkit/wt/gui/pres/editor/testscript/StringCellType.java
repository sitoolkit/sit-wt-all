package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

public class StringCellType extends SpreadsheetCellType.StringType implements TestScriptCellType {

    @Override
    public SpreadsheetCell createCell(int row, int column, String value) {
        return super.createCell(row, column, ROW_SPAN, COL_SPAN, value);
    }

}
