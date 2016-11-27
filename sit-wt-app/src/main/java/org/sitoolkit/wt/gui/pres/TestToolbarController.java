package org.sitoolkit.wt.gui.pres;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.app.test.TestService;
import org.sitoolkit.wt.gui.domain.project.ProjectState;
import org.sitoolkit.wt.gui.domain.project.ProjectState.State;
import org.sitoolkit.wt.gui.domain.test.SitWtDebugStdoutListener;
import org.sitoolkit.wt.gui.domain.test.TestRunParams;
import org.sitoolkit.wt.gui.infra.config.PropertyManager;
import org.sitoolkit.wt.gui.infra.fx.FxUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.gui.infra.util.StrUtils;
import org.sitoolkit.wt.gui.infra.util.SystemUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;

public class TestToolbarController implements Initializable {

    @FXML
    private ToolBar startGroup;

    @FXML
    private ToolBar runningGroup;

    @FXML
    private ToolBar debugGroup;

    @FXML
    private ChoiceBox<String> browserChoice;

    @FXML
    private ComboBox<String> baseUrlCombo;

    @FXML
    private Label pauseButton;

    @FXML
    private Label restartButton;

    @FXML
    private TextField stepNoText;

    @FXML
    private TextField locatorText;

    private FileTreeController fileTreeController;

    private MessageView messageView;

    private ConversationProcess testProcess;

    private ProjectState projectState;

    private SitWtDebugStdoutListener debugStdoutListener = new SitWtDebugStdoutListener();

    private TestService testService = new TestService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        browserChoice.getItems().addAll(SystemUtils.getBrowsers());

        FxUtils.bindVisible(pauseButton, debugStdoutListener.getPausingProperty().not());
        FxUtils.bindVisible(restartButton, debugStdoutListener.getPausingProperty());
    }

    public void initialize(MessageView messageView, FileTreeController fileTreeController,
            ProjectState projectState) {
        this.projectState = projectState;
        this.fileTreeController = fileTreeController;
        this.messageView = messageView;

        FxUtils.bindVisible(startGroup, projectState.isLoaded());
        FxUtils.bindVisible(runningGroup, projectState.isRunning());
        FxUtils.bindVisible(debugGroup, projectState.isDebugging());
    }

    public void loadProject() {
        List<String> baseUrls = PropertyManager.get().getBaseUrls();
        if (!baseUrls.isEmpty()) {
            baseUrlCombo.getItems().addAll(baseUrls);
            baseUrlCombo.setValue(baseUrls.get(0));
        }
    }

    public void destroy() {
        PropertyManager.get().setBaseUrls(baseUrlCombo.getItems());

        if (testProcess != null) {
            testProcess.destroy();
        }
    }

    @FXML
    public void run() {
        messageView.startMsg("テストを実行します。");
        runTest(false, false);
    }

    @FXML
    public void debug() {
        messageView.startMsg("テストをデバッグします。");
        runTest(true, false);
    }

    @FXML
    public void runParallel() {
        messageView.startMsg("テストを並列実行します。");
        runTest(false, true);
    }

    private void runTest(boolean isDebug, boolean isParallel) {
        projectState.setState(isDebug ? State.DEBUGGING : State.RUNNING);

        TestRunParams params = new TestRunParams();
        params.setScripts(fileTreeController.getSelectedFiles());
        params.setBaseDir(projectState.getBaseDir());
        params.setDebug(isDebug);
        params.setParallel(isParallel);
        params.setDriverType(getDriverType());
        params.setBaseUrl(getBaseUrl());

        addBaseUrl(params.getBaseUrl());

        ProcessExitCallback callback = exitCode -> {
            projectState.reset();
            Platform.runLater(() -> messageView.addMsg("テストを終了します。"));
        };

        ConversationProcess testProcess = testService.runTest(params, debugStdoutListener,
                callback);

        if (testProcess == null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("");
            alert.setContentText("");
            alert.setHeaderText("実行するテストスクリプトを選択してください。テストスクリプトの拡張子はxlsx、xlsx、csv、htmlです。");
            alert.show();
            projectState.reset();
        } else {
            this.testProcess = testProcess;
        }

    }

    private void addBaseUrl(String baseUrl) {
        List<String> items = baseUrlCombo.getItems();
        int limit = PropertyManager.get().getBaseUrlLimit();

        if (!items.contains(baseUrl)) {
            items.add(0, baseUrl);
        }

        if (items.size() > limit) {
            items.remove(limit);
        }
        baseUrlCombo.setValue(baseUrl);
    }

    @FXML
    public void pause() {
        testProcess.input("");
    }

    @FXML
    public void restart() {
        String stepNo = stepNoText.getText();
        if (!StrUtils.isEmpty(stepNo)) {
            testProcess.input("!" + stepNo);
            testProcess.input("#" + stepNo);
        }

        testProcess.input("s");
    }

    @FXML
    public void back() {
        testProcess.input("b");
    }

    @FXML
    public void forward() {
        testProcess.input("f");
    }

    @FXML
    public void export() {
        testProcess.input("e");
    }

    @FXML
    public void checkLocator() {
        testProcess.input("l " + locatorText.getText());
    }

    @FXML
    public void quit() {
        testProcess.destroy();
        projectState.reset();
    }

    public void setBaseUrl(String baseUrl) {
        baseUrlCombo.setValue(baseUrl);
    }

    public String getBaseUrl() {
        return baseUrlCombo.getValue();
    }

    public String getDriverType() {
        return browserChoice.getValue();
    }

}
