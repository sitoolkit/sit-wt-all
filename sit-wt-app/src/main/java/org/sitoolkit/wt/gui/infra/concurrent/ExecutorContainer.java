package org.sitoolkit.wt.gui.infra.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorContainer {

    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static ExecutorService get() {
        return executor;
    }

    public static void awaitTermination() {
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
