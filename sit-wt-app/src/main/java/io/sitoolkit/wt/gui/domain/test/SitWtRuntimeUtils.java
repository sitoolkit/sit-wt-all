package io.sitoolkit.wt.gui.domain.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.sitoolkit.wt.gui.infra.UnInitializedException;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.util.StrUtils;

public class SitWtRuntimeUtils {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(SitWtRuntimeUtils.class);

    private static String sitwtClasspath;

    private static String javaHome;

    private static final Pattern SCRIPT_FILE_PATTERN = Pattern
            .compile(".*\\.xlsx$|.*\\.xls$|.*\\.csv$|.*\\.html$");

    public static String buildScriptStr(List<File> selectedFiles) {
        return StrUtils.join(filterTestScripts(selectedFiles));
    }

    public static String buildScriptStr(File testScript, List<String> caseNos) {
        StringBuilder sb = new StringBuilder();
        String testScriptPath = testScript.getAbsolutePath();

        for (String caseNo : caseNos) {

            if (sb.length() != 0)
                sb.append(",");
            sb.append(testScriptPath);
            sb.append("#");
            sb.append(caseNo);
        }

        return sb.toString();
    }

    public static List<File> filterTestScripts(List<File> selectedFiles) {
        return selectedFiles.stream()
                .filter(file -> SCRIPT_FILE_PATTERN.matcher(file.getName()).matches())
                .collect(Collectors.toList());
    }

    public static void putJavaHome(Map<String, String> map) {

        if (javaHome == null) {
            javaHome = System.getProperty("java.home");
            LOG.info("app.setJavahome", new Object[] { javaHome });
        }
        map.put("JAVA_HOME", javaHome);

    }

    public static List<String> buildPage2ScriptCommand(String browser, String baseUrl) {
        List<String> command = buildJavaCommand();
        addVmArgs(command, browser, baseUrl);

        command.add("io.sitoolkit.wt.app.page2script.Page2Script");

        return command;
    }

    public static List<String> buildOpe2ScriptCommand(String url) {
        List<String> command = buildJavaCommand();

        if (StrUtils.isNotEmpty(url)) {
            command.add("-Durl=" + url);
        }

        command.add("io.sitoolkit.wt.app.ope2script.FirefoxOpener");

        return command;

    }

    public static List<String> buildJavaCommand() {
        List<String> command = new ArrayList<>();
        command.add("java");

        command.add("-cp");
        command.add(getSitWtClasspath());

        return command;
    }

    public static void addVmArgs(List<String> command, String browser, String baseUrl) {
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
