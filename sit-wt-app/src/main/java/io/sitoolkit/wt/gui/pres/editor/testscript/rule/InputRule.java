package io.sitoolkit.wt.gui.pres.editor.testscript.rule;

public interface InputRule {

  public boolean match(String value);

  public boolean isChangeable();

  public String defalutValue();
}
