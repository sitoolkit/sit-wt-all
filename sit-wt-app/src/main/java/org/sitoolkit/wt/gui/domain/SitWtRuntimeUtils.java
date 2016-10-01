package org.sitoolkit.wt.gui.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.gui.infra.MavenUtils;
import org.sitoolkit.wt.gui.infra.StrUtils;

public class SitWtRuntimeUtils {

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
        command.add("clean");
        command.add("verify");

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

}
