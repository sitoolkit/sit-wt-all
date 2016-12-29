package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.app.update.UpdateService;
import org.sitoolkit.wt.gui.infra.log.LogUtils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class UpdateController {

    private static final Logger LOG = LogUtils.get(UpdateController.class);

    UpdateService service = new UpdateService();

    public UpdateController() {
    }

    public void checkAndInstall() {
        try {
            File pomFile = unarchivePom();

            service.checkSitWtAppUpdate(pomFile, newVersion -> confirmAndInstall(newVersion));

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

    private void confirmAndInstall(String newVersion) {
        Platform.runLater(() -> {
            Alert conf = new Alert(AlertType.CONFIRMATION);
            conf.setContentText("SIT-WTの新しいバージョンがあります。ダウンロードしますか？");

            Optional<ButtonType> result = conf.showAndWait();

            if (result.get() == ButtonType.OK) {
                service.downloadSitWtApp(new File("."), newVersion,
                        downloadedFile -> restart(downloadedFile));

            }
        });
    }

    private void restart(File jar) {
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
