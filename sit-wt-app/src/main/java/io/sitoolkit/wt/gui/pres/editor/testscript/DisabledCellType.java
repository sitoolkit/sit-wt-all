package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public class DisabledCellType extends ReadOnlyCellType {

    public static final String LABEL = "";

    @Override
    public SpreadsheetCell createCell(int row, int column, String value) {
        return super.createCell(row, column, LABEL);
    }

}