package io.sitoolkit.wt.gui.infra.fx;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtils {

  public AlertUtils() {
    // TODO Auto-generated constructor stub
  }

  public static void info(String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("");
    alert.setContentText("");
    alert.setHeaderText(message);
    alert.show();
  }

}
