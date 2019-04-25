package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

public abstract class NormalStringCellType extends SpreadsheetCellType<String> {

    public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan,
            String value) {
        SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
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
