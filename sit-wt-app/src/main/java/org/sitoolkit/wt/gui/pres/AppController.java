package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.app.diffevidence.DiffEvidenceService;
import org.sitoolkit.wt.gui.app.project.ProjectService;
import org.sitoolkit.wt.gui.app.script.ScriptService;
import org.sitoolkit.wt.gui.app.test.TestService;
import org.sitoolkit.wt.gui.domain.project.ProjectState;
import org.sitoolkit.wt.gui.domain.project.ProjectState.State;
import org.sitoolkit.wt.gui.infra.fx.FxContext;
import org.sitoolkit.wt.gui.infra.fx.FxUtils;
import org.sitoolkit.wt.gui.infra.process.TextAreaStdoutListener;
import org.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import org.sitoolkit.wt.util.infra.process.ConversationProcess;
import org.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import org.sitoolkit.wt.util.infra.process.StdoutListenerContainer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

public class AppController implements Initializable {

    @FXML
    private HBox projectGroup;

    @FXML
    private ToolBar genScriptGroup;

    @FXML
    private ToolBar browsingGroup;

    @FXML
    private TextArea console;

    @FXML
    private Label exportButton;

    @FXML
    private Label toggleButton;

    @FXML
    private SampleToolbarController sampleToolbarController;

    @FXML
    private FileTreeController fileTreeController;

    @FXML
    private TestToolbarController testToolbarController;

    @FXML
    private DiffEvidenceToolbarController diffEvidenceToolbarController;

    @FXML
    private MenuItem sampleRunMenu;

    @FXML
    private MenuItem sampleStopMenu;

    private MessageView messageView = new MessageView();

    private ConversationProcess conversationProcess;

    private ProjectState projectState = new ProjectState();

    UpdateController updateController = new UpdateController();

    DiffEvidenceService diffEvidenceService = new DiffEvidenceService();

    TestService testService = new TestService();

    ProjectService projectService = new ProjectService();

    // private double stageHeight;
    //
    // private double stageWidth;
    //
    // @FXML
    // private Label maximizeButton;
    //
    // @FXML
    // private Label minimizeButton;
    //
    // private BooleanProperty windowMaximized = new
    // SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (!Boolean.getBoolean("skipUpdate")) {
            ExecutorContainer.get().execute(() -> updateController.checkAndInstall());
        }

        FxUtils.bindVisible(projectGroup, projectState.isLocking().not());
        FxUtils.bindVisible(genScriptGroup, projectState.isLoaded());
        FxUtils.bindVisible(browsingGroup, projectState.isBrowsing());

        // FxUtils.bindVisible(maximizeButton, windowMaximized.not());
        // FxUtils.bindVisible(minimizeButton, windowMaximized);

        messageView.setTextArea(console);
        StdoutListenerContainer.get().getListeners().add(new TextAreaStdoutListener(console));

        testToolbarController.initialize(messageView, fileTreeController, projectState);
        sampleToolbarController.initialize(messageView, testToolbarController, projectState);
        diffEvidenceToolbarController.initialize(messageView, fileTreeController, projectState);

        fileTreeController.setTestRunnable(testToolbarController);
    }

    public void postInit() {
        File pomFile = projectService.openProject(new File(""), projectState);
        if (pomFile == null) {
            openProject();
        } else {
            loadProject(pomFile);
        }
    }

    public void destroy() {
        ConversationProcessContainer.destroy();
        testToolbarController.destroy();
        fileTreeController.destroy();
        sampleToolbarController.destroy();

    }

    @FXML
    public void openProject() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("プロジェクトフォルダを選択してください。");
        dirChooser.setInitialDirectory(new File("."));

        File projectDir = dirChooser.showDialog(FxContext.getPrimaryStage());

        if (projectDir == null) {
            return;
        }

        File pomFile = projectService.openProject(projectDir, projectState);

        if (pomFile == null) {

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText(projectDir.getAbsolutePath() + "にプロジェクトを作成しますか？");

            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.get() == ButtonType.OK) {
                pomFile = projectService.createProject(projectDir, projectState);
                loadProject(pomFile);
            }

        } else {

            loadProject(pomFile);

        }

    }

    private void loadProject(File pomFile) {
        File projectDir = pomFile.getAbsoluteFile().getParentFile();
        messageView.addMsg("プロジェクトを開きます。" + projectDir.getAbsolutePath());
        fileTreeController.setFileTreeRoot(projectDir);
        FxContext.setTitie(projectDir.getAbsolutePath());
    }

    ScriptService scriptService = new ScriptService();

    @FXML
    public void page2script() {
        messageView.startMsg("ブラウザでページを表示した状態で「スクリプト生成」ボタンをクリックしてください。");

        projectState.setState(State.BROWSING);

        conversationProcess = scriptService.page2script(testToolbarController.getDriverType(),
                testToolbarController.getBaseUrl(), exitCode -> {
                    projectState.reset();
                });

    }

    @FXML
    public void quitBrowsing() {
        conversationProcess.input("q");
    }

    @FXML
    public void ope2script() {
        messageView.startMsg("ブラウザ操作の記録はFirefoxとSelenium IDE Pluginを使用します。");
        messageView.addMsg("Selenium IDEで記録したテストスクリプトをhtml形式でtestscriptディレクトリに保存してください。");

        scriptService.ope2script(testToolbarController.getBaseUrl());

    }

    @FXML
    public void export() {
        conversationProcess.input("e");
    }

    @FXML
    public void openScript() {
        conversationProcess.input("o");
    }

    @FXML
    public void quit() {
        conversationProcess.destroy();
        projectState.reset();
    }

    // @FXML
    // public void minimizeWindow() {
    // Stage primaryStage = FxContext.getPrimaryStage();
    // stageHeight = primaryStage.getHeight();
    // stageWidth = primaryStage.getWidth();
    // // TODO コンソールのサイズ設定
    // StageResizer.resize(primaryStage, 600, 90);
    // windowMaximized.set(false);
    // }
    //
    // @FXML
    // public void maximizeWindow() {
    // Stage primaryStage = FxContext.getPrimaryStage();
    // StageResizer.resize(primaryStage, stageWidth, stageHeight);
    // windowMaximized.set(true);
    // }

    @FXML
    public void settings() {
        FxContext.openFile(new File(projectState.getBaseDir(), "src/main/resources"));
    }

    @FXML
    public void help() {
        FxContext.showDocument("https://github.com/sitoolkit/sit-wt-all/wiki");
    }
}
