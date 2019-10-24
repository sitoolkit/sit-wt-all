package io.sitoolkit.wt.gui.pres.editor.testscript;

public interface InputRule {

  public boolean match(String value);

  public boolean isChangeable();

  public String defalutValue();

  public static final InputRule NO_RULE =
      new InputRule() {

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
      };
}
