package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValueListRule implements InputRule {

  private final List<String> valueList;

  @Override
  public boolean match(String value) {
    return valueList.contains(value);
  }

  @Override
  public boolean isChangeable() {
    return isChooseable();
  }

  @Override
  public String defalutValue() {
    return valueList.get(0);
  }

  @Override
  public List<String> getChoices() {
    return valueList;
  }
}
