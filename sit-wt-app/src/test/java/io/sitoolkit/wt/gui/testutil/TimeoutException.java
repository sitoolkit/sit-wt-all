package io.sitoolkit.wt.gui.testutil;

public class TimeoutException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TimeoutException() {
    }

    public TimeoutException(String timeoutMessage) {
        super(timeoutMessage);
    }

}
