package org.sitoolkit.wt.gui.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.gui.infra.MavenUtils;

public class SitWtRuntimeUtils {

    public static List<String> buildCommand(List<File> selectedFiles, boolean isDebug,
            boolean isParallel, String browser) {
        List<String> command = new ArrayList<>();

        command.add(MavenUtils.getCommand());
        command.add("clean");
        command.add("verify");

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
        if (testedClases.length() > 0) {
            command.add("-Dit.test=" + testedClases);
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

        return command;
    }

}
