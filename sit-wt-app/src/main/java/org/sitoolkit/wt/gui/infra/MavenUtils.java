package org.sitoolkit.wt.gui.infra;

import java.io.File;

public class MavenUtils {

    private static String mvnCommand = "";

    static {
        initCommand();
    }

    public static String getCommand() {
        return mvnCommand;
    }

    private static void initCommand() {
        // OS判定方法はorg.apache.commons.lang3.SystemUtilsを参照
        String osName = System.getProperty("os.name");
        boolean isOsWinows = osName.startsWith("Windows");
        boolean isOsMac = osName.startsWith("Mac");

        if (isOsMac) {
            File mvnFile = new File("/usr/local/bin/mvn");
            if (mvnFile.exists()) {
                mvnCommand = mvnFile.getAbsolutePath();
                return;
            }
        }

        String mavenHome = System.getenv("MAVEN_HOME");
        String mvn = mavenHome + "/bin/mvn";

        if (isOsMac) {
            if (new File(mvn).exists()) {
                mvnCommand = mvn;
                return;
            }
        }

        if (isOsWinows) {
            File mvnFile = new File(mvn + ".cmd");

            if (mvnFile.exists()) {
                mvnCommand = mvn;
            } else {
                mvnFile = new File(mvn + ".bat");

                if (mvnFile.exists()) {
                    mvnCommand = mvnFile.getAbsolutePath();
                }
            }
        }
    }

}
