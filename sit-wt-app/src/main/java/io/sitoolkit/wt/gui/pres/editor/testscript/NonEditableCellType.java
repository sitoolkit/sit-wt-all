package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public class NonEditableCellType extends DefaultStringCellType {

    @Override
    public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan,
            String value) {
        SpreadsheetCell cell = super.createCell(row, column, rowSpan, columnSpan, value);
        cell.setEditable(false);
        cell.getStyleClass().add("non-editable");
        return cell;
    }

}