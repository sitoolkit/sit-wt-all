package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.codehaus.plexus.util.StringUtils;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScriptEditorCell {

  public static final StringConverter<ScriptEditorCell> converter = new ScriptEditorCellConverter();

  public static ScriptEditorCell of(String value) {
    return new ScriptEditorCell(value);
  }

  private String value = "";
  private boolean debugCase = false;
  private boolean debugStep = false;

  public ScriptEditorCell(String value) {
    this.value = value;
  }

  static class ScriptEditorCellConverter extends StringConverter<ScriptEditorCell> {
    @Override
    public String toString(ScriptEditorCell object) {
      return StringUtils.defaultString(object.getValue());
    }

    @Override
    public ScriptEditorCell fromString(String string) {
      return ScriptEditorCell.of(StringUtils.defaultString(string));
    }
  }
}
