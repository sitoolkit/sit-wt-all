package org.sitoolkit.wt.gui.test;

public class ThreadUtils {

    public static void waitFor(StateChecker checker) {
        waitFor("", checker);
    }

    public static void waitFor(String timeoutMessage, StateChecker checker) {

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 10000) {

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
