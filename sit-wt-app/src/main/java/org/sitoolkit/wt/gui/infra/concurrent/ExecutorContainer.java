package org.sitoolkit.wt.gui.infra.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorContainer {

    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static ExecutorService get() {
        return executor;
    }
}
