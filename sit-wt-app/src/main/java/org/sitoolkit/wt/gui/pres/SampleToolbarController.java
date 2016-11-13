package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.domain.JettyConsoleListener;
import org.sitoolkit.wt.gui.domain.project.ProjectState;
import org.sitoolkit.wt.gui.domain.project.ProjectState.State;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.concurrent.ExecutorContainer;
import org.sitoolkit.wt.gui.infra.fx.FxUtils;
import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.TextAreaConsole;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public class SampleToolbarController implements Initializable {

    @FXML
    private HBox sampleToolbar;

    @FXML
    private Label runSampleButton;

    @FXML
    private Label stopSampleButton;

    private TextArea console;

    private TestToolbarController testToolbarController;

    private MessageView messageView;

    private ConversationProcess sampleProcess = new ConversationProcess();

    private ProjectState projectState;

    private BooleanProperty running = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initialize(TextArea console, MessageView messageView,
            TestToolbarController testToolbarController, ProjectState projectState) {
        this.projectState = projectState;
        this.console = console;
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

        sampleProcess.start(new TextAreaConsole(console), projectState.getBaseDir(),
                SitWtRuntimeUtils.buildSampleCommand());

        sampleProcess.onExit(exitCode -> {

            File sampledir = new File(projectState.getBaseDir(), "sample");
            if (!sampledir.exists()) {
                sampledir.mkdirs();
            }

            JettyConsoleListener listener = new JettyConsoleListener();
            sampleProcess.start(new TextAreaConsole(console, listener), sampledir,
                    MavenUtils.getCommand());

            ExecutorContainer.get().execute(() -> {
                if (listener.isSuccess()) {
                    String sampleBaseUrl = "http://localhost:8280";
                    messageView.addMsg("サンプルWebサイトを起動しました。" + sampleBaseUrl + "/input.html");
                    messageView.addMsg(
                            "サンプルテストスクリプトtestscript/SampleTestScript.xlsxを左のツリーで選択して実行できます。");
                    // TODO URLの動的取得
                    testToolbarController.setBaseUrl(sampleBaseUrl);
                    running.set(true);
                } else {
                    messageView.addMsg("サンプルWebサイトの起動に失敗しました。");
                    running.set(false);
                }
                projectState.reset();
            });

        });
    }

    @FXML
    public void stopSample() {
        messageView.startMsg("サンプルWebサイトを停止します。");
        File sampledir = new File(projectState.getBaseDir(), "sample");
        sampleProcess.start(new TextAreaConsole(console), sampledir, MavenUtils.getCommand(),
                "jetty:stop");

        sampleProcess.onExit(exitCode -> {

            Platform.runLater(() -> {
                running.set(false);
                messageView.addMsg("サンプルWebサイトを停止しました。");
            });
        });
    }

}
