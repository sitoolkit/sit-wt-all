package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class ValueListRule implements ChooseableRule {

  List<String> valueList;

  @Override
  public boolean match(String value) {
    return valueList.contains(value);
  }

  @Override
  public boolean isChangeable() {
    return valueList.size() > 1;
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
