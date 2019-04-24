package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

public abstract class DefaultStringCellType extends SpreadsheetCellType<String>
        implements TestScriptCellType {

    public SpreadsheetCell createCell(int row, int column, String value) {
        SpreadsheetCell cell = new SpreadsheetCellBase(row, column, ROW_SPAN, COL_SPAN, this);
        cell.setItem(convertValue(value));
        return cell;
    }

    @Override
    public String toString(String object) {
        return convertValue(object);
    }

    @Override
    public boolean match(Object value) {
        return true;
    }

    @Override
    public String convertValue(Object value) {
        return (String) value;
    }

}
