package io.sitoolkit.wt.util.infra.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SystemUtils {

    private static boolean windows;
    private static boolean osx;

    static {
        // OS判定方法はorg.apache.commons.lang3.SystemUtilsを参照
        String osName = System.getProperty("os.name");
        windows = osName.startsWith("Windows");
        osx = osName.startsWith("Mac");
    }

    public static boolean isWindows() {
        return windows;
    }

    public static boolean isOsX() {
        return osx;
    }

    public static List<String> getBrowsers() {
        List<String> browsers = new ArrayList<>();

        browsers.add("chrome");
        browsers.add("firefox");

        if (isWindows()) {
            browsers.add("ie");
            browsers.add("edge");
        }

        if (isOsX()) {
            // browsers.add("safari");
            browsers.add("ios");
        }

        browsers.add("android");
        browsers.add("remote");

        return browsers;
    }

    public static File getSitRepository() {
        File repo = isWindows() ? new File(System.getenv("ProgramData"), "sitoolkit/repository")
                : new File(System.getProperty("user.home"), ".sitoolkit/repository");

        if (!repo.exists()) {
            repo.mkdirs();
        }

        return repo;
    }

    public static String getEnvironmentInfo() {
        String info = Stream.of("java.version", "java.vendor", "os.name", "os.arch", "os.version")
                .map(key -> String.format("%s=%s", key, System.getProperty(key)))
                .collect(Collectors.joining(", "));

        return info;
    }

}
