package io.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

import io.sitoolkit.wt.gui.app.test.TestService;
import io.sitoolkit.wt.gui.domain.project.ProjectState;
import io.sitoolkit.wt.gui.domain.project.ProjectState.State;
import io.sitoolkit.wt.gui.domain.test.DebugListenerFinder;
import io.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import io.sitoolkit.wt.gui.domain.test.TestRunParams;
import io.sitoolkit.wt.gui.infra.config.PropertyManager;
import io.sitoolkit.wt.gui.infra.fx.FxUtils;
import io.sitoolkit.wt.util.infra.process.ConversationProcess;
import io.sitoolkit.wt.util.infra.process.ProcessExitCallback;
import io.sitoolkit.wt.util.infra.util.SystemUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import lombok.Getter;

public class TestToolbarController implements Initializable, TestRunnable {

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
    private ToggleButton compareToggle;

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

    private String sessionId;

    @Getter
    private BooleanProperty pausing = new SimpleBooleanProperty(false);

    private ProjectState projectState;

    private DebugListenerFinder debugListenerFinder;

    TestService testService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        browserChoice.getItems().addAll(SystemUtils.getBrowsers());

        // FxUtils.bindVisible(pauseButton,
        // debugStdoutListener.getPausingProperty().not());
        // FxUtils.bindVisible(restartButton,
        // debugStdoutListener.getPausingProperty());
        FxUtils.bindVisible(pauseButton, pausing.not());
        FxUtils.bindVisible(restartButton, pausing);
    }

    public void initialize(MessageView messageView, FileTreeController fileTreeController,
            ProjectState projectState, DebugListenerFinder debugListenerFinder) {
        this.projectState = projectState;
        this.fileTreeController = fileTreeController;
        this.messageView = messageView;
        this.debugListenerFinder = debugListenerFinder;

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

        testService.destroy();
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

    @Override
    public void runTest(boolean isDebug, boolean isParallel, File testScript,
            List<String> caseNos) {
        messageView.startMsg("テストを実行します。");
        runTest(isDebug, false, SitWtRuntimeUtils.buildScriptStr(testScript, caseNos));
    }

    private void runTest(boolean isDebug, boolean isParallel) {
        runTest(isDebug, isParallel,
                SitWtRuntimeUtils.buildScriptStr(fileTreeController.getSelectedItems(true)));
    }

    private void runTest(boolean isDebug, boolean isParallel, String targetScriptStr) {
        projectState.setState(isDebug ? State.DEBUGGING : State.RUNNING);

        TestRunParams params = new TestRunParams();
        params.setTargetScripts(targetScriptStr);
        params.setBaseDir(projectState.getBaseDir());
        params.setDebug(isDebug);
        params.setParallel(isParallel);
        params.setCompareScreenshot(compareToggle.isSelected());
        params.setDriverType(getDriverType());
        params.setBaseUrl(getBaseUrl());

        if (isDebug && !isParallel) {
            List<Path> scriptPaths = SitWtRuntimeUtils.decodeScrintStr(targetScriptStr);
            if (scriptPaths.size() == 1) {
                debugListenerFinder.find(scriptPaths.get(0)).ifPresent(params::setDebugListener);
            }
        }

        addBaseUrl(params.getBaseUrl());

        ProcessExitCallback callback = exitCode -> {
            projectState.reset();
            Platform.runLater(() -> messageView.addMsg("テストを終了します。"));
        };

        // ConversationProcess testProcess = testService.runTest(params,
        // debugStdoutListener,
        // callback);
        String sessionId = testService.runTest(params, callback);

        // if (testProcess == null) {
        if (sessionId == null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("");
            alert.setContentText("");
            alert.setHeaderText("実行するテストスクリプトを選択してください。テストスクリプトの拡張子はcsv、htmlです。");
            alert.show();
            projectState.reset();
        } else {
            // this.testProcess = testProcess;
            this.sessionId = sessionId;
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
        // testProcess.input("");
        testService.pause(sessionId);
        pausing.set(true);
    }

    @FXML
    public void restart() {
        String stepNo = stepNoText.getText();
        testService.restart(sessionId, stepNo);
        pausing.set(false);
    }

    @FXML
    public void back() {
        testService.back(sessionId);
    }

    @FXML
    public void forward() {
        testService.forward(sessionId);
    }

    @FXML
    public void export() {
        testService.export(sessionId);
    }

    @FXML
    public void checkLocator() {
        String locatorStr = locatorText.getText();
        testService.checkLocator(sessionId, locatorStr);
    }

    @FXML
    public void quit() {
        // testProcess.destroy();
        testService.stopTest(sessionId);
        projectState.reset();
    }

    public void setBaseUrl(String baseUrl) {
        baseUrlCombo.setValue(baseUrl);
    }

    public String getBaseUrl() {
        return this.baseUrlCombo.getEditor().getText().trim();
    }

    public String getDriverType() {
        return browserChoice.getValue();
    }

}
