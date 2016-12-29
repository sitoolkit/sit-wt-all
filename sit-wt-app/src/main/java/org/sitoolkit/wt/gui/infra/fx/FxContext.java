package org.sitoolkit.wt.gui.infra.fx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.log.LogUtils;
import org.sitoolkit.wt.gui.infra.util.SystemUtils;

import javafx.application.HostServices;
import javafx.stage.Stage;

public class FxContext {

    private static final Logger LOG = LogUtils.get(FxContext.class);

    private static Stage primaryStage;

    private static HostServices hostServices;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        FxContext.primaryStage = primaryStage;
    }

    public static void setTitie(String title) {
        primaryStage.setTitle("SIT-WT " + title);
    }

    public static HostServices getHostServices() {
        return hostServices;
    }

    public static void setHostServices(HostServices hostServices) {
        FxContext.hostServices = hostServices;
    }

    public static void openFile(File file) {

        if (file.isDirectory() && SystemUtils.isOsX()) {
            try {
                Runtime.getRuntime().exec(new String[] { "open", file.getAbsolutePath() });
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "", e);
            }
        }

        try {
            hostServices.showDocument(file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public static void showDocument(String uri) {
        hostServices.showDocument(uri);
    }

}
