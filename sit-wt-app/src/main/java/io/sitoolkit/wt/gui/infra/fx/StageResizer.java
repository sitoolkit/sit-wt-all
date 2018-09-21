package io.sitoolkit.wt.gui.infra.fx;

import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import javafx.application.Platform;
import javafx.stage.Stage;

public class StageResizer {

    public static void resize(Stage stage, double width, double height) {

        ExecutorContainer.get().execute(() -> {
            final int LOOP_COUNT = 20;
            double deltaWidth = (width - stage.getWidth()) / LOOP_COUNT;
            double deltaHeight = (height - stage.getHeight()) / LOOP_COUNT;

            for (int i = 0; i < LOOP_COUNT; i++) {
                Platform.runLater(() -> {
                    stage.setWidth(stage.getWidth() + deltaWidth);
                    stage.setHeight(stage.getHeight() + deltaHeight);
                });

                try {
                    Thread.sleep(10L);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            stage.setWidth(width);
            stage.setHeight(height);
        });
    }

}
