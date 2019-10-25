package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class FreeRule implements InputRule {

  @Getter private static final InputRule instance = new FreeRule();

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
