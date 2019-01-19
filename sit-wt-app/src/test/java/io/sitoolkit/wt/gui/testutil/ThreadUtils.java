package io.sitoolkit.wt.gui.testutil;

public class ThreadUtils {

    private static final long DEFAULT_TIMEOUT = 30000;

    public static void waitFor(StateChecker checker) {
        waitFor("", checker);
    }

    public static void waitFor(String timeoutMessage, StateChecker checker) {
        waitFor(timeoutMessage, DEFAULT_TIMEOUT, checker);
    }

    public static void waitFor(String timeoutMessage, long timeout, StateChecker checker) {

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeout) {

            if (checker.check()) {
                return;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        throw new TimeoutException(timeoutMessage);

    }

    public static interface StateChecker {
        boolean check();
    }
}
