package org.sitoolkit.wt.infra;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThreadUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MultiThreadUtils.class);

    public static <T> T submitWithProgress(Callable<T> callable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<T> future = executor.submit(callable);

        while (!future.isDone()) {

            System.out.print(".");

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOG.warn("スレッドの待機で例外が発生しました", e);
            }

        }
        System.out.println("");
        executor.shutdown();

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

}
