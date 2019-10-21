package io.sitoolkit.wt.gui.pres.editor.testscript;

import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ScriptEditorTableCell extends TableCell<ScriptEditorRow, ScriptEditorCell> {

  private TextField textField;
  private static final StringConverter<ScriptEditorCell> converter = ScriptEditorCell.converter;

  public ScriptEditorTableCell() {
    this.getStyleClass().add("sit-wt-script-editor-table-cell");
  }

  @Override
  public void startEdit() {
    if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
      return;
    }
    super.startEdit();

    if (isEditing()) {
      if (textField == null) {
        textField = CellUtils.createTextField(this, converter);
      }

      CellUtils.startEdit(this, converter, null, null, textField);
    }
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    CellUtils.cancelEdit(this, converter, null);
  }

  @Override
  public void updateItem(ScriptEditorCell item, boolean empty) {
    super.updateItem(item, empty);
    CellUtils.updateItem(this, converter, null, null, textField);

    if (!empty) {
      updateStyle();
    }
  }

  private void updateStyle() {
    getStyleClass().remove("debugCase");
    getStyleClass().remove("debugStep");

    if (getItem().isDebugCase()) {
      getStyleClass().add("debugCase");
    }

    if (getItem().isDebugStep()) {
      getStyleClass().add("debugStep");
    }
  }
}
