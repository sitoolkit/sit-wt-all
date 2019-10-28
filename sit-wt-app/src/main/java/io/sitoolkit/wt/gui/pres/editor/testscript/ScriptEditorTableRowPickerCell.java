package io.sitoolkit.wt.gui.pres.editor.testscript;

import javafx.scene.control.TableCell;

class ScriptEditorTableRowPickerCell extends TableCell<ScriptEditorRow, ScriptEditorCell> {

  ScriptEditorTableRowPickerCell() {
    getStyleClass().add("test-step-picker");
  }

  @Override
  public void updateItem(ScriptEditorCell cell, boolean empty) {
    super.updateItem(cell, empty);

    if (!empty) {
      setText("");
      getStyleClass().remove("debugStep");
      getStyleClass().remove("enabled");

      if (cell.isDebugStep()) {
        getStyleClass().add("debugStep");
      }
      if (cell.isBreakpoint()) {
        getStyleClass().add("enabled");
      }
    }
  }
}
