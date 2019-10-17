package io.sitoolkit.wt.gui.pres;

import java.net.URL;
import java.util.ResourceBundle;
import javax.annotation.Resource;
import io.sitoolkit.wt.gui.app.sample.SampleService;
import io.sitoolkit.wt.gui.domain.project.ProjectState;
import io.sitoolkit.wt.gui.domain.project.ProjectState.State;
import io.sitoolkit.wt.gui.domain.sample.SampleStartedCallback;
import io.sitoolkit.wt.gui.infra.fx.FxUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;

public class SampleToolbarController implements Initializable {

  @FXML
  private HBox sampleToolbar;

  @FXML
  private Label runSampleButton;

  @FXML
  private Label stopSampleButton;

  private TestToolbarController testToolbarController;

  private MessageView messageView;

  private ProjectState projectState;

  @Getter
  private BooleanProperty running = new SimpleBooleanProperty(false);

  @Resource
  SampleService service;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void initialize(MessageView messageView, TestToolbarController testToolbarController,
      ProjectState projectState) {
    this.projectState = projectState;
    this.testToolbarController = testToolbarController;
    this.messageView = messageView;

    FxUtils.bindVisible(sampleToolbar, projectState.isLoaded());
    FxUtils.bindVisible(runSampleButton, running.not());
    FxUtils.bindVisible(stopSampleButton, running);

  }

  @FXML
  public void runSample() {
    projectState.setState(State.LOCKING);
    messageView.startMsg("サンプルWebサイトを起動します。");

    service.create(projectState.getBaseDirPath());
    service.start(8280, projectState.getBaseDirPath(), onStarted());
  }

  private SampleStartedCallback onStarted() {
    return success -> {
      if (success) {
        String sampleBaseUrl = "http://localhost:8280";
        messageView.addMsg("サンプルWebサイトを起動しました。" + sampleBaseUrl + "/input.html");
        messageView.addMsg("サンプルテストスクリプトtestscript/SampleTestScript.csvを左のツリーで選択して実行できます。");
        // TODO サンプルURLの動的取得
        testToolbarController.setBaseUrl(sampleBaseUrl);
        running.set(true);
      } else {
        messageView.addMsg("サンプルWebサイトの起動に失敗しました。");
        running.set(false);
      }
      projectState.reset();
    };
  }

  @FXML
  public void stopSample() {
    messageView.startMsg("サンプルWebサイトを停止します。");

    service.stop(retCode -> {
      running.set(false);
      Platform.runLater(() -> {
        messageView.addMsg("サンプルWebサイトを停止しました。");
      });
    });
  }

  public void destroy() {
    service.stop();
  }

}
