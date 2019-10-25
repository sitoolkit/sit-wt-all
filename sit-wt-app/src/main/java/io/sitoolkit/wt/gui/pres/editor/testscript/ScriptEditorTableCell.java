package io.sitoolkit.wt.gui.pres.editor.testscript;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.converter.DefaultStringConverter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ScriptEditorTableCell extends TableCell<ScriptEditorRow, ScriptEditorCell> {

  private TextField textField;
  private ChoiceBox<String> choiceBox;
  private Control editingControl;

  @Override
  public void startEdit() {
    if (!isEditable()
        || !getTableView().isEditable()
        || !getTableColumn().isEditable()
        || !getItem().isEditable()) {
      return;
    }
    super.startEdit();

    if (isEditing()) {
      editingControl = prepareEditControl();
      setText(null);
      setGraphic(editingControl);
      editingControl.requestFocus();
    }
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    setText(getItemText());
    setGraphic(null);
  }

  @Override
  public void updateItem(ScriptEditorCell item, boolean empty) {
    super.updateItem(item, empty);
    if (isEmpty()) {
      setText(null);
      setGraphic(null);
    } else {
      if (isEditing()) {
        if (editingControl != null) {
          setTextToControl(editingControl, getItemText());
        }
        setText(null);
        setGraphic(editingControl);
      } else {
        setText(getItemText());
        setGraphic(null);
      }
      updateStyle();
    }
  }

  private void updateStyle() {
    getStyleClass().remove("debugCase");
    getStyleClass().remove("debugStep");
    getStyleClass().remove("non-editable");

    if (getItem().isDebugCase()) {
      getStyleClass().add("debugCase");
    }
    if (getItem().isDebugStep()) {
      getStyleClass().add("debugStep");
    }
    if (!getItem().isEditable()) {
      getStyleClass().add("non-editable");
    }
  }

  private String getItemText() {
    return getItem().getValue();
  }

  private void commitEdit(String text) {
    if (getItem().getInputRule().match(text)) {
      commitEdit(getItem().toBuilder().value(text).build());
    }
  }

  private Control prepareEditControl() {
    if (getItem() != null && getItem().isChoice()) {
      if (choiceBox == null) {
        choiceBox = createChoiceBox();
      }
      choiceBox.setItems(FXCollections.observableList(getItem().getChoices()));
      setTextToControl(choiceBox, getItemText());
      return choiceBox;
  
    } else {
      if (textField == null) {
        textField = createTextField();
      }
      setTextToControl(textField, getItemText());
      textField.selectAll();
      return textField;
    }
  }

  private ChoiceBox<String> createChoiceBox() {
    ChoiceBox<String> choice = new ChoiceBox<>();
    choice.setMaxWidth(Double.MAX_VALUE);
    choice.setConverter(new DefaultStringConverter());
    choice
        .showingProperty()
        .addListener(
            o -> {
              if (!choice.isShowing()) {
                commitEdit(choice.getSelectionModel().getSelectedItem());
              }
            });
    return choice;
  }

  private TextField createTextField() {
    final TextField textInput = new TextField();

    // Use onAction here rather than onKeyReleased (with check for Enter),
    // as otherwise we encounter RT-34685
    textInput.setOnAction(
        event -> {
          commitEdit(textInput.getText());
          event.consume();
        });
    textInput.setOnKeyReleased(
        t -> {
          if (t.getCode() == KeyCode.ESCAPE) {
            cancelEdit();
            t.consume();
          }
        });
    return textInput;
  }

  private void setTextToControl(Control editControl, String text) {
    if (editControl instanceof TextField) {
      TextField field = (TextField) editControl;
      field.setText(text);
  
    } else if (editControl instanceof ChoiceBox) {
      @SuppressWarnings("unchecked")
      ChoiceBox<String> choice = (ChoiceBox<String>) editControl;
      choice.getSelectionModel().select(text);
  
    } else {
      throw new IllegalArgumentException("not supported control:" + editControl);
    }
  }
}
