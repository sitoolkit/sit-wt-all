package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.codehaus.plexus.util.StringUtils;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ScriptEditorCell {

  private String value;
  private boolean debugCase;
  private boolean debugStep;

  public static ScriptEditorCell of(String value) {
    return new ScriptEditorCell(value, false, false);
  }

  public static final StringConverter<ScriptEditorCell> converter = new ScriptEditorCellConverter();

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
