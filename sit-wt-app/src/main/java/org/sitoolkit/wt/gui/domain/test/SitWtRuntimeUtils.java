package org.sitoolkit.wt.gui.domain.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.sitoolkit.wt.gui.infra.UnInitializedException;
import org.sitoolkit.wt.gui.infra.util.StrUtils;

public class SitWtRuntimeUtils {

    private static final Logger LOG = Logger.getLogger(SitWtRuntimeUtils.class.getName());

    private static String sitwtClasspath;

    private static String javaHome;

    private static final Pattern SCRIPT_FILE_PATTERN = Pattern
            .compile(".*\\.xlsx$|.*\\.xls$|.*\\.csv$|.*\\.html$");

    public static List<File> filterTestScripts(List<File> selectedFiles) {
        return selectedFiles.stream()
                .filter(file -> SCRIPT_FILE_PATTERN.matcher(file.getName()).matches())
                .collect(Collectors.toList());
    }

    public static void putJavaHome(Map<String, String> map) {

        if (javaHome == null) {
            javaHome = System.getProperty("java.home");
            LOG.log(Level.INFO, "set JAVA_HOME to {0}", new Object[] { javaHome });
        }
        map.put("JAVA_HOME", javaHome);

    }

    public static List<String> buildPage2ScriptCommand(String browser, String baseUrl) {
        List<String> command = buildJavaCommand();
        addVmArgs(command, browser, baseUrl);

        command.add("org.sitoolkit.wt.app.page2script.Page2Script");

        return command;
    }

    public static List<String> buildOpe2ScriptCommand(String url) {
        List<String> command = buildJavaCommand();

        if (StrUtils.isNotEmpty(url)) {
            command.add("-Durl=" + url);
        }

        command.add("org.sitoolkit.wt.app.ope2script.FirefoxOpener");

        return command;

    }

    public static List<String> buildJavaCommand() {
        List<String> command = new ArrayList<>();
        command.add("java");

        command.add("-cp");
        command.add(getSitWtClasspath());

        return command;
    }

    static void addVmArgs(List<String> command, String browser, String baseUrl) {
        command.add("-Ddriver.type=" + browser);
        command.add("-Dsitwt.cli=false");

        if (StrUtils.isNotEmpty(baseUrl)) {
            command.add("-DbaseUrl=" + baseUrl);
        }

    }

    static String getSitWtClasspath() {
        if (sitwtClasspath == null) {
            throw new UnInitializedException();
        }
        return sitwtClasspath;
    }

    public static void setSitWtClasspath(String sitwtClasspath) {
        SitWtRuntimeUtils.sitwtClasspath = sitwtClasspath;
    }
}
