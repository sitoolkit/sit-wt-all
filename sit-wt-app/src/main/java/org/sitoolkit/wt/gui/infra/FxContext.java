package org.sitoolkit.wt.gui.infra;

import javafx.stage.Stage;

public class FxContext {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        FxContext.primaryStage = primaryStage;
    }

    public static void setTitie(String title) {
        primaryStage.setTitle("SIT-WT " + title);
    }
}
