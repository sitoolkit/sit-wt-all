package io.sitoolkit.wt.gui.pres.editor.testscript;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class CellUtils {

  private CellUtils() {};

  /* General convenience */

  private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
    return converter == null
        ? cell.getItem() == null ? "" : cell.getItem().toString()
        : converter.toString(cell.getItem());
  }

  /* ********************* */
  /* ChoiceBox convenience */
  /* ********************* */

  static <T> void updateItem(
      final Cell<T> cell, final StringConverter<T> converter, final ChoiceBox<T> choiceBox) {
    updateItem(cell, converter, null, null, choiceBox);
  }

  static <T> void updateItem(
      final Cell<T> cell,
      final StringConverter<T> converter,
      final HBox hbox,
      final Node graphic,
      final ChoiceBox<T> choiceBox) {
    if (cell.isEmpty()) {
      cell.setText(null);
      cell.setGraphic(null);
    } else {
      if (cell.isEditing()) {
        if (choiceBox != null) {
          choiceBox.getSelectionModel().select(cell.getItem());
        }
        cell.setText(null);

        if (graphic != null) {
          hbox.getChildren().setAll(graphic, choiceBox);
          cell.setGraphic(hbox);
        } else {
          cell.setGraphic(choiceBox);
        }
      } else {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(graphic);
      }
    }
  };

  static <T> ChoiceBox<T> createChoiceBox(
      final Cell<T> cell,
      final ObservableList<T> items,
      final ObjectProperty<StringConverter<T>> converter) {
    ChoiceBox<T> choiceBox = new ChoiceBox<T>(items);
    choiceBox.setMaxWidth(Double.MAX_VALUE);
    choiceBox.converterProperty().bind(converter);
    choiceBox
        .showingProperty()
        .addListener(
            o -> {
              if (!choiceBox.isShowing()) {
                cell.commitEdit(choiceBox.getSelectionModel().getSelectedItem());
              }
            });
    return choiceBox;
  }

  /* ********************* */
  /* TextField convenience */
  /* ********************* */

  static <T> void updateItem(
      final Cell<T> cell,
      final StringConverter<T> converter,
      final HBox hbox,
      final Node graphic,
      final TextField textField) {
    if (cell.isEmpty()) {
      cell.setText(null);
      cell.setGraphic(null);
    } else {
      if (cell.isEditing()) {
        if (textField != null) {
          textField.setText(getItemText(cell, converter));
        }
        cell.setText(null);

        if (graphic != null) {
          hbox.getChildren().setAll(graphic, textField);
          cell.setGraphic(hbox);
        } else {
          cell.setGraphic(textField);
        }
      } else {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(graphic);
      }
    }
  }

  static <T> void startEdit(
      final Cell<T> cell,
      final StringConverter<T> converter,
      final HBox hbox,
      final Node graphic,
      final TextField textField) {
    if (textField != null) {
      textField.setText(getItemText(cell, converter));
    }
    cell.setText(null);

    if (graphic != null) {
      hbox.getChildren().setAll(graphic, textField);
      cell.setGraphic(hbox);
    } else {
      cell.setGraphic(textField);
    }

    textField.selectAll();

    // requesting focus so that key input can immediately go into the
    // TextField (see RT-28132)
    textField.requestFocus();
  }

  static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter, Node graphic) {
    cell.setText(getItemText(cell, converter));
    cell.setGraphic(graphic);
  }

  static <T> TextField createTextField(final Cell<T> cell, final StringConverter<T> converter) {
    final TextField textField = new TextField(getItemText(cell, converter));

    // Use onAction here rather than onKeyReleased (with check for Enter),
    // as otherwise we encounter RT-34685
    textField.setOnAction(
        event -> {
          if (converter == null) {
            throw new IllegalStateException(
                "Attempting to convert text input into Object, but provided "
                    + "StringConverter is null. Be sure to set a StringConverter "
                    + "in your cell factory.");
          }
          cell.commitEdit(converter.fromString(textField.getText()));
          event.consume();
        });
    textField.setOnKeyReleased(
        t -> {
          if (t.getCode() == KeyCode.ESCAPE) {
            cell.cancelEdit();
            t.consume();
          }
        });
    return textField;
  }
}
