package io.sitoolkit.wt.domain.operation;

public class VerifyObj {

    private String verifyCol;

    private String expected;

    private String actual;

    private String log;

    private String errorLog;

    public String getVerifyCol() {
        return verifyCol;
    }

    public void setVerifyCol(String verifyCol) {
        this.verifyCol = verifyCol;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

}
