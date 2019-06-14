package io.sitoolkit.wt.gui.pres;

import java.net.URL;
import java.util.ResourceBundle;
import javax.annotation.Resource;
import io.sitoolkit.wt.gui.app.diffevidence.DiffEvidenceService;
import io.sitoolkit.wt.gui.domain.project.ProjectState;
import io.sitoolkit.wt.gui.infra.fx.FxUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;

public class DiffEvidenceToolbarController implements Initializable {

  @FXML
  private HBox diffEvidenceToolbar;

  private FileTreeController fileTreeController;

  private MessageView messageView;

  private ProjectState projectState;

  @Resource
  private DiffEvidenceService diffEvidenceService;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void initialize(MessageView messageView, FileTreeController fileTreeController,
      ProjectState projectState) {
    this.projectState = projectState;
    this.fileTreeController = fileTreeController;
    this.messageView = messageView;

    FxUtils.bindVisible(diffEvidenceToolbar, projectState.isLoaded());
  }

  @FXML
  public void maskEvidence() {

    messageView.startMsg("スクリーンショットにマスク処理を施したエビデンスを生成します。");

    boolean success = diffEvidenceService.genMaskEvidence(fileTreeController.getSelectedItem());
    projectState.reset();

    if (!success) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("");
      alert.setContentText("");
      alert.setHeaderText("マスク対象のエビデンスディレクトリを1つ選択してください。");
      alert.show();
    }
  }

  @FXML
  public void setBaseEvidence() {

    messageView.startMsg("基準エビデンス確定処理を実行します。");

    boolean success = diffEvidenceService.setBaseEvidence(fileTreeController.getSelectedItem());
    projectState.reset();

    if (!success) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("");
      alert.setContentText("");
      alert.setHeaderText("基準エビデンスとして確定するエビデンスディレクトリを1つ選択してください。");
      alert.show();
    }
  }

  @FXML
  public void genDiffEvidence() {

    messageView.startMsg("比較エビデンスを生成します。");

    boolean success = diffEvidenceService.genDiffEvidence(fileTreeController.getRoot(),
        fileTreeController.getSelectedItems(false));
    projectState.reset();

    if (!success) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("");
      alert.setContentText("");
      alert
          .setHeaderText("比較対象のエビデンスディレクトリを1つ、または2つ選択してください。1つだけ選択した場合は、選択したディレクトリと基準エビデンスを比較します。");
      alert.show();
    }
  }

}
