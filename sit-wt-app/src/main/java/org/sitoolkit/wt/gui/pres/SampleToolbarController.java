package org.sitoolkit.wt.gui.pres;

import java.net.URL;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.app.sample.SampleService;
import org.sitoolkit.wt.gui.domain.project.ProjectState;
import org.sitoolkit.wt.gui.domain.project.ProjectState.State;
import org.sitoolkit.wt.gui.domain.sample.SampleStartedCallback;
import org.sitoolkit.wt.gui.infra.fx.FxUtils;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

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

    private BooleanProperty running = new SimpleBooleanProperty(false);

    SampleService service = new SampleService();

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

        service.create(projectState.getBaseDir(), sampledir -> {

            service.start(projectState.getBaseDir(), onStarted());

        });
    }

    private SampleStartedCallback onStarted() {
        return success -> {
            if (success) {
                String sampleBaseUrl = "http://localhost:8280";
                messageView.addMsg("サンプルWebサイトを起動しました。" + sampleBaseUrl + "/input.html");
                messageView
                        .addMsg("サンプルテストスクリプトtestscript/SampleTestScript.csvを左のツリーで選択して実行できます。");
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

        service.stop(projectState.getBaseDir(), () -> {
            running.set(false);
            Platform.runLater(() -> {
                messageView.addMsg("サンプルWebサイトを停止しました。");
            });
        });
    }

    public void destroy() {
        service.stop(projectState.getBaseDir());
    }

}
