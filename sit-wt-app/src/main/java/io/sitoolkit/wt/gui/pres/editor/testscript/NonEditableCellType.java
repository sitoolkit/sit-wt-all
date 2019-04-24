package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public class NonEditableCellType extends DefaultStringCellType {

    @Override
    public SpreadsheetCell createCell(int row, int column, String value) {
        SpreadsheetCell cell = super.createCell(row, column, value);
        cell.setEditable(false);
        cell.getStyleClass().add("non-editable");
        return cell;
    }

}