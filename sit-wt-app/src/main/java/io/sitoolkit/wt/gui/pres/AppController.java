package io.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

import io.sitoolkit.util.buidtoolhelper.process.StdoutListenerContainer;
import io.sitoolkit.wt.gui.app.diffevidence.DiffEvidenceService;
import io.sitoolkit.wt.gui.app.project.ProjectService;
import io.sitoolkit.wt.gui.app.script.ScriptService;
import io.sitoolkit.wt.gui.app.test.TestService;
import io.sitoolkit.wt.gui.domain.project.ProjectState;
import io.sitoolkit.wt.gui.domain.project.ProjectState.State;
import io.sitoolkit.wt.gui.infra.fx.FxContext;
import io.sitoolkit.wt.gui.infra.fx.FxUtils;
import io.sitoolkit.wt.gui.infra.log.TextAreaOutputStream;
import io.sitoolkit.wt.gui.infra.process.TextAreaStdoutListener;
import io.sitoolkit.wt.infra.log.DelegatingOutputStreamAppender;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import io.sitoolkit.wt.util.infra.process.ConversationProcess;
import io.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
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
    private Label openButton;

    @FXML
    private Label saveButton;

    @FXML
    private Label saveAsButton;

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

    @FXML
    private TabPane editorTab;

    @FXML
    private MenuBarController menuBarController;

    private MessageView messageView = new MessageView();

    private ConversationProcess conversationProcess;

    private ProjectState projectState = new ProjectState();

    UpdateController updateController = new UpdateController();

    EditorTabController editorTabController = new EditorTabController();

    DiffEvidenceService diffEvidenceService = new DiffEvidenceService();

    TestService testService = new TestService();

    ProjectService projectService = new ProjectService();

    ScriptService scriptService = new ScriptService();

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

        DelegatingOutputStreamAppender.setStaticOutputStream(new TextAreaOutputStream(console));

        FxUtils.bindVisible(projectGroup, projectState.isLocking().not());
        FxUtils.bindVisible(genScriptGroup, projectState.isLoaded());
        FxUtils.bindVisible(browsingGroup, projectState.isBrowsing());

        // FxUtils.bindVisible(maximizeButton, windowMaximized.not());
        // FxUtils.bindVisible(minimizeButton, windowMaximized);

        messageView.setTextArea(console);
        StdoutListenerContainer.getInstance().getStdoutListeners().add(new TextAreaStdoutListener(console));
        StdoutListenerContainer.getInstance().getStderrListeners().add(new TextAreaStdoutListener(console));

        testToolbarController.initialize(messageView, fileTreeController, projectState);
        testToolbarController.testService = testService;
        sampleToolbarController.initialize(messageView, testToolbarController, projectState);
        diffEvidenceToolbarController.initialize(messageView, fileTreeController, projectState);

        fileTreeController.setTestRunnable(testToolbarController);
        fileTreeController.fileOpenable = editorTabController;
        fileTreeController.scriptService = scriptService;

        editorTabController.tabs = editorTab;
        editorTabController.scriptService = scriptService;
        editorTabController.initialize();
        FxUtils.bindDisable(saveButton, editorTabController.isEmpty());
        FxUtils.bindDisable(saveAsButton, editorTabController.isEmpty());

        testService.setDebugListener(editorTabController);

        menuBarController.setProjectState(projectState);
        menuBarController.setAppController(this);
        menuBarController.setEditorTabController(editorTabController);
        menuBarController.setTestToolbarController(testToolbarController);
        menuBarController.setDiffEvidenceToolbarController(diffEvidenceToolbarController);
        menuBarController.setSampleToolbarController(sampleToolbarController);
        menuBarController.initialize();
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

        if (!Boolean.getBoolean("skipUpdate")) {
            ExecutorContainer.get().execute(() -> updateController.checkAndInstall());
        }

        System.setProperty("sitwt.projectDirectory", projectDir.getAbsolutePath());

    }

    private void loadProject(File pomFile) {
        File projectDir = pomFile.getAbsoluteFile().getParentFile();
        messageView.addMsg("プロジェクトを開きます。" + projectDir.getAbsolutePath());
        fileTreeController.setFileTreeRoot(projectDir);
        testToolbarController.loadProject();
        scriptService.loadProject();
        FxContext.setTitie(projectDir.getAbsolutePath());
    }

    @FXML
    public void editScript() {
        editorTabController.open();
    }

    @FXML
    public void editor2script() {
        editorTabController.save();
    }

    @FXML
    public void editor2scriptAs() {
        editorTabController.saveAs();
    }

    @FXML
    public void page2script() {
        messageView.startMsg("ブラウザでページを表示した状態で「スクリプト生成」ボタンをクリックしてください。");

        projectState.setState(State.BROWSING);

        scriptService.page2script(testToolbarController.getDriverType(), testToolbarController.getBaseUrl());
    }

    @FXML
    public void quitBrowsing() {
        scriptService.quitBrowsing();
        projectState.reset();
    }

    @FXML
    public void ope2script() {
        messageView.startMsg("ブラウザ操作の記録はFirefoxとSelenium IDE Pluginを使用します。");
        messageView.addMsg("Selenium IDEで記録したテストスクリプトをhtml形式でtestscriptディレクトリに保存してください。");

        scriptService.ope2script(testToolbarController.getBaseUrl());

    }

    @FXML
    public void export() {
        Path exportScript = scriptService.export();
        editorTabController.open(exportScript.toFile());
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
