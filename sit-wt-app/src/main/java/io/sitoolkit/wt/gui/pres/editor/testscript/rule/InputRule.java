package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import java.util.Collections;
import java.util.List;

public interface InputRule {

  boolean match(String value);

  boolean isChangeable();

  String defalutValue();

  default List<String> getChoices() {
    return Collections.emptyList();
  };

  default boolean isChooseable() {
    return getChoices() != null && getChoices().size() > 1;
  }

  default String convertValue(String value) {
    if (!match(value)) {
      throw new IllegalArgumentException(
          "input value dosen't match input rule : value=" + value + ", rule=" + this);
    } else {
      return value;
    }
  };
}
