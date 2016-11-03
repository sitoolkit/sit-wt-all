package org.sitoolkit.wt.gui.pres;

import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.concurrent.ExecutorContainer;
import org.sitoolkit.wt.gui.infra.config.PropertyManager;
import org.sitoolkit.wt.gui.infra.fx.FxContext;
import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.util.LogUtils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class App extends Application {

    private static final Logger LOG = LogUtils.get(App.class);

    private AppController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            LOG.log(Level.SEVERE, "unexpected exception", throwable);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("エラーが発生しました。");
            alert.show();
        });

        FxContext.setPrimaryStage(primaryStage);
        FxContext.setHostServices(getHostServices());

        LogUtils.init();
        Executors.newSingleThreadExecutor().submit(() -> MavenUtils.findAndInstall());

        primaryStage.setTitle("SI-Toolkit for Web Testing");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/App.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root);
        // scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Material+Icons");
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        controller.destroy();
        PropertyManager.get().save();
        ExecutorContainer.get().shutdown();
    }
}
