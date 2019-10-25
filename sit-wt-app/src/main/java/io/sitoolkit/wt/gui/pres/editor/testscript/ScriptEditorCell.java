package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.List;
import org.codehaus.plexus.util.StringUtils;
import io.sitoolkit.wt.gui.pres.editor.testscript.rule.FreeRule;
import io.sitoolkit.wt.gui.pres.editor.testscript.rule.InputRule;
import javafx.util.StringConverter;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ScriptEditorCell {

  private String value;
  @Builder.Default private InputRule inputRule = FreeRule.getInstance();
  private boolean debugCase;
  private boolean debugStep;

  public static final StringConverter<ScriptEditorCell> converter = new ScriptEditorCellConverter();

  public static ScriptEditorCell of(String value) {
    return builder().value(value).build();
  }

  public ScriptEditorCell(
      String value, @NonNull InputRule inputRule, boolean debugCase, boolean debugStep) {
    this.inputRule = inputRule;
    this.value = inputRule.convertValue(value);
    this.debugCase = debugCase;
    this.debugStep = debugStep;
  }

  public boolean isEditable() {
    return inputRule.isChangeable();
  }

  public boolean isChoice() {
    return inputRule.isChooseable();
  }

  public List<String> getChoices() {
    return inputRule.getChoices();
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
