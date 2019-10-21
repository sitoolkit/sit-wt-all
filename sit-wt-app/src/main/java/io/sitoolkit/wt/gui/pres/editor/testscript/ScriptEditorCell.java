package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

  public boolean isChoice() {
    return !getChoices().isEmpty();
  }

  public List<String> getChoices() {
    switch (StringUtils.defaultString(getValue())) {
      case "choice":
        return Arrays.asList("texiField", "choice", "country", "city");

      case "country":
        return Arrays.asList("texiField", "choice", "country", "US", "UK", "France", "Japan");

      case "city":
        return Arrays.asList("texiField", "choice", "city", "L.A.", "N.Y.", "Paris", "London");

      case "Japan":
        return Arrays.asList("texiField", "choice", "Japan", "Tokyo", "Osaka", "Nagoya");
      default:
        return Collections.emptyList();
    }
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
