package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class SpreadsheetUtils {

  /**
   * On Windows 10, changing the picker class or cell types may not rewrite the display, so call
   * hideColumn/showColumn to force a redraw. It is not hideRow/showRow because it causes an error
   * when you click outside the spreadsheet after changing the operation value.
   * 
   * @param spreadSheet Spreadsheet to redraw
   */
  public static void forceRedraw(SpreadsheetView spreadSheet) {
    SpreadsheetColumn column = spreadSheet.getColumns().get(0);
    spreadSheet.hideColumn(column);
    spreadSheet.showColumn(column);
  }

}
