package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class ScriptEditorTableCell extends TableCell<ScriptEditorRow, ScriptEditorCell> {

  private static final StringConverter<ScriptEditorCell> converter = ScriptEditorCell.converter;

  private TextField textField;
  private ChoiceBox<String> choiceBox;

  private String getItemText() {
    return converter.toString(getItem());
  }

  public ScriptEditorTableCell() {
    this.getStyleClass().add("sit-wt-script-editor-table-cell");
  }

  private boolean isChoice() {
    return getItem() != null && getItem().isChoice();
  }

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
      if (isChoice()) {
        if (choiceBox == null) {
          choiceBox = createChoiceBox(this::onCommit);
        }
        choiceBox.setItems(FXCollections.observableList(getItem().getChoices()));
        choiceBox.getSelectionModel().select(getItemText());
        setText(null);
        setGraphic(choiceBox);
        choiceBox.requestFocus();

      } else {
        if (textField == null) {
          textField = createTextField(this::onCommit, this::cancelEdit);
        }
        textField.setText(getItemText());
        textField.selectAll();
        setText(null);
        setGraphic(textField);

        // requesting focus so that key input can immediately go into the
        // TextField (see RT-28132)
        textField.requestFocus();
      }
    }
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    setText(getItemText());
    setGraphic(null);
  }

  void onCommit(String value) {
    if (getItem().getInputRule().match(value)) {
      commitEdit(getItem().toBuilder().value(value).build());
    }
  }

  private void setText(Control editControl, String text) {
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

  @Override
  public void updateItem(ScriptEditorCell item, boolean empty) {
    super.updateItem(item, empty);
    if (isEmpty()) {
      setText(null);
      setGraphic(null);
    } else {
      if (isEditing()) {
        Control c = isChoice() ? choiceBox : textField;
        if (c != null) {
          setText(c, getItemText());
        }
        setText(null);
        setGraphic(c);
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

  static ChoiceBox<String> createChoiceBox(final Consumer<String> commitListener) {
    ChoiceBox<String> choiceBox = new ChoiceBox<>();
    choiceBox.setMaxWidth(Double.MAX_VALUE);
    choiceBox.setConverter(new DefaultStringConverter());
    choiceBox
        .showingProperty()
        .addListener(
            o -> {
              if (!choiceBox.isShowing()) {
                commitListener.accept(choiceBox.getSelectionModel().getSelectedItem());
              }
            });
    return choiceBox;
  }

  static TextField createTextField(
      final Consumer<String> commitListener, final Runnable cancelListener) {
    final TextField textField = new TextField();

    // Use onAction here rather than onKeyReleased (with check for Enter),
    // as otherwise we encounter RT-34685
    textField.setOnAction(
        event -> {
          commitListener.accept(textField.getText());
          event.consume();
        });
    textField.setOnKeyReleased(
        t -> {
          if (t.getCode() == KeyCode.ESCAPE) {
            cancelListener.run();
            t.consume();
          }
        });
    return textField;
  }
}
