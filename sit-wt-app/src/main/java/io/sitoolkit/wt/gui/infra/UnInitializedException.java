package io.sitoolkit.wt.gui.infra;

public class UnInitializedException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -4500311262149145737L;

  public UnInitializedException() {
    super("プロジェクトが正しく読み込まれていません。");
  }

}
