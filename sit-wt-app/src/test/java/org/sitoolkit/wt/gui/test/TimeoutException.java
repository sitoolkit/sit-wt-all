package org.sitoolkit.wt.gui.test;

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
