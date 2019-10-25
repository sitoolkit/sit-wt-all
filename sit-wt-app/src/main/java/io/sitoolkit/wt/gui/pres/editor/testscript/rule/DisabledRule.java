package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

import java.util.Arrays;
import lombok.Getter;

public class DisabledRule extends ValueListRule {

  @Getter private static DisabledRule instance = new DisabledRule();

  private DisabledRule() {
    super(Arrays.asList(""));
  }
}
