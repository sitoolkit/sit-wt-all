package org.sitoolkit.wt.gui.infra.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.LogConsole;
import org.sitoolkit.wt.gui.infra.util.FileIOUtils;
import org.sitoolkit.wt.gui.infra.util.StrUtils;
import org.sitoolkit.wt.gui.infra.util.SystemUtils;

public class MavenUtils {

    private static final Logger LOG = Logger.getLogger(MavenUtils.class.getName());

    private static String mvnCommand = "";

    public static String getCommand() {
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

    public static void findAndInstall() {
        mvnCommand = find();
        if (mvnCommand == null || mvnCommand.isEmpty()) {
            mvnCommand = install();
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
            String mvn = children[0].getAbsolutePath() + "/bin/mvn";
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
        }

        return "";

        // File[] children = destDir.listFiles();
        // return children.length == 0 ? "" : children[0].getAbsolutePath();
    }

    public static void buildDownloadArtifactCommand(String artifact, File destDir,
            DownloadCallback callback) {

        List<String> command = new ArrayList<>();
        command.add(getCommand());
        command.add("dependency:copy");
        command.add("-Dartifact=" + artifact);
        command.add("-DoutputDirectory=" + destDir.getAbsolutePath());

        ConversationProcess process = new ConversationProcess();
        process.start(new LogConsole(), new File("."), command);

        process.onExit(exitCode -> {
            if (exitCode == 0) {
                LOG.log(Level.INFO, "downloaded : {0}", destDir.getAbsolutePath());
                callback.onDownloaded();
            } else {
                LOG.log(Level.WARNING, "fail to download :", artifact);
            }
        });
    }

    public static void checkUpdate(File pom, String artifact, VersionCheckMode mode,
            UpdateCallback callback) {

        LOG.log(Level.INFO, "check update for {0} in {1}",
                new Object[] { artifact, pom.getAbsolutePath() });

        List<String> command = new ArrayList<>();
        command.add(MavenUtils.getCommand());
        command.add(mode.getPluginGoal());
        command.add("-f");
        command.add(pom.getAbsolutePath());

        ConversationProcess process = new ConversationProcess();
        MavenVersionsListener listener = new MavenVersionsListener(mode.getUpdateLine(),
                artifact + " ..");
        process.start(new LogConsole(listener), pom.getParentFile(), command);

        process.onExit(exitCode -> {
            String newVersion = listener.getNewVersion();

            if (StrUtils.isEmpty(newVersion)) {
                LOG.log(Level.INFO, "latest artifact : {0}", artifact);
            } else {
                LOG.log(Level.INFO, "new version is found : {0}", newVersion);
                if (callback != null) {
                    callback.callback(listener.getNewVersion());
                }
            }
        });
    }
}
