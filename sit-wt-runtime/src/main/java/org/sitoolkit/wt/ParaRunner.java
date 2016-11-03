package org.sitoolkit.wt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class ParaRunner {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ForkJoinPool pool = new ForkJoinPool(2);

        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            map.put(i, i);
        }

        List<Integer> list = new ArrayList<>();

        pool.submit(() -> map.keySet().stream().parallel()
                .forEach(val -> System.out
                        .println(Thread.currentThread().getName() + " " + list.add(out(val)))))
                .get();
    }

    static int out(int val) {
        System.out.println(Thread.currentThread().getName() + " " + val);
        return val;
    }
}
