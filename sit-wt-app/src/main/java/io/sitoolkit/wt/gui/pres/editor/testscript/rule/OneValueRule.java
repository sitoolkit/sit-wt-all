package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class OneValueRule implements InputRule {

  String onlyOneValue;

  @Override
  public boolean match(String value) {
    return onlyOneValue.equals(value);
  }

  @Override
  public boolean isChangeable() {
    return false;
  }

  @Override
  public String defalutValue() {
    return onlyOneValue;
  }
}
