package io.sitoolkit.wt.gui.pres.editor.testscript;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScriptEditorRow {

  private StringProperty column1;
  private StringProperty column2;

  public void setColumn1(String value) {
    column1Property().set(value);
  }

  public void setColumn2(String value) {
    column2Property().set(value);
  }

  public String getColumn1() {
    return column1Property().get();
  }

  public String getColumn2() {
    return column2Property().get();
  }

  public StringProperty column1Property() {
    if (column1 == null) column1 = new SimpleStringProperty(this, "column1");
    return column1;
  }

  public StringProperty column2Property() {
    if (column2 == null) column2 = new SimpleStringProperty(this, "column2");
    return column2;
  }
}
