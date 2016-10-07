package org.sitoolkit.wt.gui.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.FileIOUtils;
import org.sitoolkit.wt.gui.infra.MavenUtils;
import org.sitoolkit.wt.gui.infra.PropertyManager;
import org.sitoolkit.wt.gui.infra.StrUtils;
import org.sitoolkit.wt.gui.infra.SystemUtils;
import org.sitoolkit.wt.gui.infra.UnExpectedException;

public class SitWtRuntimeUtils {

    private static final Logger LOG = Logger.getLogger(SitWtRuntimeUtils.class.getName());

    public static String findTestedClasses(List<File> selectedFiles) {
        StringBuilder testedClases = new StringBuilder();
        for (File file : selectedFiles) {

            if (!file.isFile()) {
                continue;
            }

            String baseName = file.getName().replaceFirst("\\.xlsx$|\\\\.xls$|\\.csv$", "");

            if (baseName.equals(file.getName())) {
                continue;
            }

            if (testedClases.length() > 0) {
                testedClases.append(",");
            }

            testedClases.append(baseName);
            testedClases.append("IT");
        }

        return testedClases.toString();
    }

    public static List<String> buildCommand(String testedClasses, boolean isDebug,
            boolean isParallel, String browser, String baseUrl) {
        List<String> command = new ArrayList<>();

        command.add(MavenUtils.getCommand());
        command.add("verify");

        command.add("-T");
        command.add("1C");

        if (StrUtils.isNotEmpty(testedClasses)) {
            command.add("-Dit.test=" + testedClasses);
        }

        List<String> profiles = new ArrayList<>();
        if (isDebug) {
            profiles.add("debug");
        }
        if (isParallel) {
            profiles.add("parallel");
        }
        if (!profiles.isEmpty()) {
            command.add("-P" + String.join(",", profiles));
        }

        command.add("-Ddriver.type=" + browser);

        if (StrUtils.isNotEmpty(baseUrl)) {
            command.add("-DbaseUrl=" + baseUrl);
        }

        return command;
    }

    private static void createClasspathFile(File pomFile, File outputFile) {

        List<String> command = new ArrayList<>();
        command.add(MavenUtils.getCommand());

        command.add("dependency:build-classpath");
        command.add("-f");
        command.add(pomFile.getAbsolutePath());
        command.add("-Dmdep.outputFile=" + outputFile.getAbsolutePath());

        ProcessBuilder builder = new ProcessBuilder(command);

        try {
            Process process = builder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new UnExpectedException(e);
        }
    }

    private static File getSitWtClasspathFile() {
        return new File(SystemUtils.getSitRepository(), "sit-wt-classpath");
    }

    public static String getTestRunnerClasspath(File pomFile) {
        File sitWtClasspathFile = getSitWtClasspathFile();

        if (!sitWtClasspathFile.exists()) {
            createClasspathFile(pomFile, sitWtClasspathFile);
        }

        return FileIOUtils.file2str(sitWtClasspathFile);
    }

    public static List<String> buildSingleTestCommand(List<File> scriptFiles, boolean isDebug,
            String browser, String baseUrl) {
        List<String> command = buildJavaCommand();
        addVmArgs(command, browser, baseUrl);

        command.add("-cp");
        command.add(PropertyManager.get().getClasspath());

        command.add("org.sitoolkit.wt.app.test.TestRunner");

        command.add(StrUtils.join(scriptFiles));

        return command;
    }

    public static List<String> buildPage2ScriptCommand(String browser, String baseUrl) {
        List<String> command = buildJavaCommand();
        addVmArgs(command, browser, baseUrl);

        command.add("org.sitoolkit.wt.app.page2script.Page2Script");

        return command;
    }

    private static List<String> buildJavaCommand() {
        List<String> command = new ArrayList<>();
        command.add("java");

        command.add("-cp");
        command.add(PropertyManager.get().getClasspath());

        return command;
    }

    private static void addVmArgs(List<String> command, String browser, String baseUrl) {
        command.add("-Ddriver.type=" + browser);

        if (StrUtils.isNotEmpty(baseUrl)) {
            command.add("-DbaseUrl=" + baseUrl);
        }

    }
}
