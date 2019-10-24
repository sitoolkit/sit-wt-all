package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.codehaus.plexus.util.StringUtils;
import javafx.util.StringConverter;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ScriptEditorCell {

  private String value;
  @Builder.Default private InputRule inputRule = InputRule.NO_RULE;
  private boolean debugCase;
  private boolean debugStep;

  public static final StringConverter<ScriptEditorCell> converter = new ScriptEditorCellConverter();

  public static ScriptEditorCell of(String value) {
    return builder().value(value).build();
  }

  public ScriptEditorCell(
      String value, @NonNull InputRule inputRule, boolean debugCase, boolean debugStep) {
    if (!inputRule.match(value)) {
      throw new IllegalArgumentException(
          "input value dosen't match input rule : value=" + value + ", rule=" + inputRule);
    }
    this.inputRule = inputRule;
    this.value = value;
    this.debugCase = debugCase;
    this.debugStep = debugStep;
  }

  public boolean isEditable() {
    return inputRule.isChangeable();
  }

  public boolean isChoice() {
    return !getChoices().isEmpty();
  }

  public List<String> getChoices() {
    switch (StringUtils.defaultString(getValue())) {
      case "choice":
        return Arrays.asList("text", "choice", "country", "city", "uneditable");

      case "country":
        return Arrays.asList(
            "text", "choice", "country", "US", "UK", "France", "Japan", "uneditable");

      case "city":
        return Arrays.asList(
            "text", "choice", "city", "L.A.", "N.Y.", "Paris", "London", "uneditable");

      case "Japan":
        return Arrays.asList("text", "choice", "Japan", "Tokyo", "Osaka", "Nagoya", "uneditable");

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
