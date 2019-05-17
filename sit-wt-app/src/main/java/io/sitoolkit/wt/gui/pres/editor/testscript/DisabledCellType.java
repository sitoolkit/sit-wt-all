package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public class DisabledCellType extends ReadOnlyCellType {

  public static final String LABEL = "";

  @Override
  public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan,
      String value) {
    return createCell(row, column, rowSpan, columnSpan);
  }

  public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan) {
    return super.createCell(row, column, rowSpan, columnSpan, LABEL);
  }

}
