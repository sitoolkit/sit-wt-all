package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.List;
import io.sitoolkit.wt.gui.pres.editor.testscript.rule.FreeRule;
import io.sitoolkit.wt.gui.pres.editor.testscript.rule.InputRule;
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
  private boolean breakpoint;

  public static ScriptEditorCell of(String value) {
    return builder().value(value).build();
  }

  public ScriptEditorCell(
      String value,
      @NonNull InputRule inputRule,
      boolean debugCase,
      boolean debugStep,
      boolean breakpoint) {
    this.inputRule = inputRule;
    this.value = inputRule.convertValue(value);
    this.debugCase = debugCase;
    this.debugStep = debugStep;
    this.breakpoint = breakpoint;
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
}
