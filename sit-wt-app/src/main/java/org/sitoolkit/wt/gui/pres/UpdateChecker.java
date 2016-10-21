package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.maven.DownloadCallback;
import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.maven.VersionCheckMode;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class UpdateChecker {

    private static final Logger LOG = Logger.getLogger(UpdateChecker.class.getName());

    private UpdateChecker() {
    }

    public static void checkAndInstall() {
        try {
            File pom = unarchivePom();

            MavenUtils.checkUpdate(pom, "org.sitoolkit.wt:sit-wt-all", VersionCheckMode.PARENT,
                    newVersion -> {

                        confirmAndInstall(newVersion, () -> {
                            restart(new File("sit-wt-app-" + newVersion + ".jar"));
                        });

                    });

        } catch (IOException e) {
            LOG.log(Level.WARNING, "can't unarchive pom.xml", e);
        }

    }

    private static File unarchivePom() throws IOException {
        URL url = ClassLoader
                .getSystemResource("META-INF/maven/org.sitoolkit.wt/sit-wt-app/pom.xml");
        File pom = new File("sit-wt-app-pom-" + System.currentTimeMillis() + ".xml");
        Files.copy(url.openStream(), pom.toPath());
        pom.deleteOnExit();

        return pom;
    }

    private static void confirmAndInstall(String newVersion, DownloadCallback callback) {
        Platform.runLater(() -> {
            Alert conf = new Alert(AlertType.CONFIRMATION);
            conf.setContentText("SIT-WTの新しいバージョンがあります。ダウンロードしますか？");

            Optional<ButtonType> result = conf.showAndWait();

            if (result.get() == ButtonType.OK) {
                MavenUtils.buildDownloadArtifactCommand("org.sitoolkit.wt:sit-wt-app:" + newVersion,
                        new File(""), callback);

            }
        });
    }

    private static void restart(File jar) {
        Platform.runLater(() -> {
            Alert conf = new Alert(AlertType.CONFIRMATION);
            conf.setContentText("新しいバージョンは再起動後に有効になります。再起動しますか？");

            Optional<ButtonType> result = conf.showAndWait();

            if (result.get() == ButtonType.OK) {
                ProcessBuilder builder = new ProcessBuilder();
                builder.command("java", "-jar", jar.getAbsolutePath());
                LOG.log(Level.INFO, "execute {0}", builder.command());

                try {
                    builder.start();
                    Platform.exit();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "fail to restart", e);
                }
            }

        });
    }
}
