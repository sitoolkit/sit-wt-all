package io.sitoolkit.wt.infra.process;

public class ExecuteResult {

  private String stdout;

  private String stderr;

  private int exitValue;

  public ExecuteResult() {}

  public ExecuteResult(String stdout, String stderr, int exitValue) {
    super();
    this.stdout = stdout;
    this.stderr = stderr;
    this.exitValue = exitValue;
  }

  public String getStdout() {
    return stdout;
  }

  public void setStdout(String stdout) {
    this.stdout = stdout;
  }

  public String getStderr() {
    return stderr;
  }

  public void setStderr(String stderr) {
    this.stderr = stderr;
  }

  public int getExitValue() {
    return exitValue;
  }

  public void setExitValue(int exitValue) {
    this.exitValue = exitValue;
  }

}
