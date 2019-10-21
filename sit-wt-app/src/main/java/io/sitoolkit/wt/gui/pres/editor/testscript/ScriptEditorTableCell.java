package io.sitoolkit.wt.gui.pres.editor.testscript;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ScriptEditorTableCell extends TableCell<ScriptEditorRow, ScriptEditorCell> {

  private static final StringConverter<ScriptEditorCell> converter = ScriptEditorCell.converter;

  private TextField textField;

  private ObservableList<ScriptEditorCell> items;
  private ChoiceBox<ScriptEditorCell> choiceBox;

  public ScriptEditorTableCell() {
    this.getStyleClass().add("sit-wt-script-editor-table-cell");
    items = FXCollections.observableArrayList();
    items.add(ScriptEditorCell.of("text-field"));
    items.add(ScriptEditorCell.of("choice"));
  }

  private boolean isChoice() {
    return getItem() == null ? false : getItem().isChoice();
  }

  @Override
  public void startEdit() {
    if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
      return;
    }
    if (isChoice()) {
      if (choiceBox == null) {
        choiceBox = CellUtils.createChoiceBox(this, items, converter);
      }
      choiceBox.getSelectionModel().select(getItem());
    }
    super.startEdit();
    if (isChoice()) {
      setText(null);
      setGraphic(choiceBox);
    } else {
      if (isEditing()) {
        if (textField == null) {
          textField = CellUtils.createTextField(this, converter);
        }

        CellUtils.startEdit(this, converter, null, null, textField);
      }
    }
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    if (isChoice()) {
      setText(converter.toString(getItem()));
      setGraphic(null);
    } else {
      CellUtils.cancelEdit(this, converter, null);
    }
  }

  @Override
  public void updateItem(ScriptEditorCell item, boolean empty) {
    super.updateItem(item, empty);
    if (isChoice()) {
      CellUtils.updateItem(this, converter, null, null, choiceBox);
    } else {
      CellUtils.updateItem(this, converter, null, null, textField);
    }
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
