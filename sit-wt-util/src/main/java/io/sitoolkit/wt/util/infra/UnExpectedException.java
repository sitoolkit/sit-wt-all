package io.sitoolkit.wt.util.infra;

public class UnExpectedException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = -4741737562740700154L;

  public UnExpectedException() {}

  public UnExpectedException(String message) {
    super(message);
  }

  public UnExpectedException(Throwable cause) {
    super(cause);
  }

  public UnExpectedException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnExpectedException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
