package org.sitoolkit.wt.gui.pres;

import org.sitoolkit.wt.gui.infra.FxContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FxContext.setPrimaryStage(primaryStage);

        primaryStage.setTitle("SI-Toolkit for Web Testing");

        Parent root = FXMLLoader.load(getClass().getResource("/App.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setMinHeight(primaryStage.getHeight());
    }

}
