package org.sitoolkit.wt.util.infra.util;

import java.text.NumberFormat;

public class Stopwatch {

    private static final NumberFormat DEFAULT_NUMBER_FORMAT = NumberFormat.getInstance();

    private static ThreadLocal<Long> threadLocalStartTime = new ThreadLocal<>();

    public static void start() {
        threadLocalStartTime.set(System.currentTimeMillis());
    }

    public static String end() {
       Long startTime = threadLocalStartTime.get();
       long currentTime = System.currentTimeMillis();

       if (startTime == null) {
           return "N/A";
       }

       return DEFAULT_NUMBER_FORMAT.format(currentTime - startTime) + "ms";
    }

}
