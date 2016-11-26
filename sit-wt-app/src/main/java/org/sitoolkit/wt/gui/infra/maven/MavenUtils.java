package org.sitoolkit.wt.gui.infra.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.UnExpectedException;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;
import org.sitoolkit.wt.gui.infra.util.FileIOUtils;
import org.sitoolkit.wt.gui.infra.util.StrUtils;
import org.sitoolkit.wt.gui.infra.util.SystemUtils;

public class MavenUtils {

    private static final Logger LOG = Logger.getLogger(MavenUtils.class.getName());

    private static String mvnCommand = "";

    public static List<String> getCommand(ProcessParams params) {
        List<String> mvnCommand = new ArrayList<String>();
        mvnCommand.add(getCommand());

        params.setCommand(mvnCommand);

        Map<String, String> enviroment = new HashMap<>();
        enviroment.put("JAVA_HOME", System.getProperty("java.home"));
        params.setEnviroment(enviroment);

        return mvnCommand;
    }

    private static String getCommand() {
        while (mvnCommand == null || mvnCommand.isEmpty()) {
            LOG.info("wait for installing Maven...");

            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                LOG.log(Level.WARNING, "", e);
            }
        }

        return mvnCommand;
    }

    public static synchronized void findAndInstall() {
        if (StrUtils.isEmpty(mvnCommand)) {
            mvnCommand = find();
            if (StrUtils.isEmpty(mvnCommand)) {
                mvnCommand = install();
            }
        }
        LOG.info("mvn command is '" + mvnCommand + "'");
    }

    public static String find() {
        LOG.info("finding installed maven");
        if (SystemUtils.isOsX()) {
            File mvnFile = new File("/usr/local/bin/mvn");
            if (mvnFile.exists()) {
                return mvnFile.getAbsolutePath();
            }
        }

        String mavenHome = System.getenv("MAVEN_HOME");
        String mvn = mavenHome + "/bin/mvn";

        if (SystemUtils.isOsX()) {
            if (new File(mvn).exists()) {
                return mvn;
            }
        }

        if (SystemUtils.isWindows()) {
            File mvnFile = new File(mvn + ".cmd");

            if (mvnFile.exists()) {
                return mvnFile.getAbsolutePath();
            } else {
                mvnFile = new File(mvn + ".bat");

                if (mvnFile.exists()) {
                    return mvnFile.getAbsolutePath();
                }
            }
        }

        return "";
    }

    public static String install() {
        File sitRepo = SystemUtils.getSitRepository();

        // TODO 外部化
        String url = "https://archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip";
        String fileName = url.substring(url.lastIndexOf("/"));
        File destFile = new File(sitRepo, "maven/download/" + fileName);

        if (destFile.exists()) {
            LOG.info("Maven binary is already downloaded : " + destFile.getAbsolutePath());
        } else {
            FileIOUtils.download(url, destFile);
        }

        File destDir = new File(sitRepo, "maven/runtime");
        FileIOUtils.unarchive(destFile, destDir);

        File[] children = destDir.listFiles();
        if (children.length != 0) {
            String mavenHome = SystemUtils.isOsX() && ".DS_Store".equals(children[0].getName())
                    ? children[1].getAbsolutePath() : children[0].getAbsolutePath();
            String mvn = mavenHome + "/bin/mvn";
            if (SystemUtils.isOsX()) {
                if (new File(mvn).exists()) {
                    ProcessBuilder pb = new ProcessBuilder();
                    pb.command("chmod", "777", mvn);
                    try {
                        Process process = pb.start();
                        process.waitFor();
                        LOG.log(Level.INFO, "process {0} starts {1}",
                                new Object[] { process, pb.command() });
                    } catch (IOException | InterruptedException e) {
                        throw new UnExpectedException(e);
                    }
                    return mvn;
                }
            }

            if (SystemUtils.isWindows()) {
                File mvnFile = new File(mvn + ".cmd");
                if (mvnFile.exists()) {
                    return mvnFile.getAbsolutePath();
                } else {
                    mvnFile = new File(mvn + ".bat");
                    if (mvnFile.exists()) {
                        return mvnFile.getAbsolutePath();
                    }
                }
            }
        }

        return "";

        // File[] children = destDir.listFiles();
        // return children.length == 0 ? "" : children[0].getAbsolutePath();
    }

}
