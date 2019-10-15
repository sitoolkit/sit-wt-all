package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.List;
import org.controlsfx.control.spreadsheet.GridChange;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

public class ClipboardScriptAccessorSpreadsheetImpl implements ClipboardScriptAccessor {

  private static final DataFormat DATAFORMAT_SPREADSHEET;

  static {
    DataFormat fmt;
    if ((fmt = DataFormat.lookupMimeType("SpreadsheetView")) == null) {
      fmt = new DataFormat("SpreadsheetView");
    }
    DATAFORMAT_SPREADSHEET = fmt;
  }

  private TestScriptEditor editor;

  public ClipboardScriptAccessorSpreadsheetImpl(TestScriptEditor editor) {
    this.editor = editor;
  }

  public void pasteCase() {
    Clipboard cb = Clipboard.getSystemClipboard();
    if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

      @SuppressWarnings("unchecked")
      List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
      int count = editor.getCaseCount(changeList);
      if (count > 0) {
        if (editor.insertTestCases(count)) {
          editor.pasteClipboard();
        }
      }
    }
  }

  public void pasteStep() {
    Clipboard cb = Clipboard.getSystemClipboard();
    if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

      @SuppressWarnings("unchecked")
      List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
      int count = editor.getStepCount(changeList);
      if (count > 0) {
        if (editor.insertTestSteps(count)) {
          editor.pasteClipboard();
        }
      }
    }
  }

  public void pasteCaseTail() {
    Clipboard cb = Clipboard.getSystemClipboard();
    if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

      @SuppressWarnings("unchecked")
      List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
      int count = editor.getCaseCount(changeList);
      if (count > 0) {
        editor.appendTestCases(count);
        editor.pasteClipboard();
      }
    }
  }

  public void pasteStepTail() {
    Clipboard cb = Clipboard.getSystemClipboard();
    if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

      @SuppressWarnings("unchecked")
      List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
      int count = editor.getStepCount(changeList);
      if (count > 0) {
        editor.appendTestSteps(count);
        editor.pasteClipboard();
      }
    }
  }

  public boolean hasClipboardSteps() {
    Clipboard cb = Clipboard.getSystemClipboard();
    if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {
      @SuppressWarnings("unchecked")
      List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
      return editor.getStepCount(changeList) > 0;

    } else {
      return false;
    }
  }

  public boolean hasClipboardCases() {
    Clipboard cb = Clipboard.getSystemClipboard();
    if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {
      @SuppressWarnings("unchecked")
      List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
      return editor.getCaseCount(changeList) > 0;

    } else {
      return false;
    }
  }

  @Override
  public boolean hasClipboardCells() {
    // TODO Auto-generated method stub
    return false;
  }
}
