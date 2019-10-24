package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class NothingRule implements InputRule {

  @Getter private static InputRule instance = new NothingRule();

  @Override
  public boolean match(String value) {
    return value != null;
  }

  @Override
  public boolean isChangeable() {
    return true;
  }

  @Override
  public String defalutValue() {
    return "";
  }
}
