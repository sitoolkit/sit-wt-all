package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public interface TestScriptCellType {

    public static final int COL_SPAN = 1;
    public static final int ROW_SPAN = 1;

    SpreadsheetCell createCell(int row, int column, String value);
}
