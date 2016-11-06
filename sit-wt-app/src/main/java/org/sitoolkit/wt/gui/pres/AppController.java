package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.app.project.ProjectService;
import org.sitoolkit.wt.gui.app.test.TestService;
import org.sitoolkit.wt.gui.domain.JettyConsoleListener;
import org.sitoolkit.wt.gui.domain.project.ProjectState;
import org.sitoolkit.wt.gui.domain.project.ProjectState.State;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.concurrent.ExecutorContainer;
import org.sitoolkit.wt.gui.infra.fx.FxContext;
import org.sitoolkit.wt.gui.infra.fx.FxUtils;
import org.sitoolkit.wt.gui.infra.fx.StageResizer;
import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.TextAreaConsole;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AppController implements Initializable {

    @FXML
    private ToolBar genScriptGroup;

    @FXML
    private ToolBar browsingGroup;

    @FXML
    private TextArea console;

    @FXML
    private Button exportButton;

    @FXML
    private Button toggleButton;

    @FXML
    private FileTreeController fileTreeController;

    @FXML
    private TestToolbarController testToolbarController;

    @FXML
    private MenuItem sampleRunMenu;

    @FXML
    private MenuItem sampleStopMenu;

    private MessageView messageView = new MessageView();

    private TextAreaConsole textAreaConsole;

    private ConversationProcess conversationProcess = new ConversationProcess();

    private ProjectState projectState = new ProjectState();

    TestService testService = new TestService();

    ProjectService projectService = new ProjectService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (!Boolean.getBoolean("skipUpdate")) {
            ExecutorContainer.get().execute(() -> UpdateChecker.checkAndInstall());
        }

        FxUtils.bindVisible(genScriptGroup, projectState.isLoaded());
        FxUtils.bindVisible(browsingGroup, projectState.isBrowsing());

        sampleRunMenu.disableProperty().bind(projectState.isLoaded().not());

        FxUtils.bindVisible(maximizeButton, windowMaximized.not());
        FxUtils.bindVisible(minimizeButton, windowMaximized);

        messageView.setTextArea(console);
        textAreaConsole = new TextAreaConsole(console);

        // TODO プロジェクトの初期化判定はpom.xml内にSIT-WTの設定があること
        File pomFile = projectService.openProject(new File(""));
        if (pomFile == null) {
            messageView.addMsg("[プロジェクト]メニュー>[新規作成]からプロジェクトを作成するフォルダを選択してください。");
            messageView.addMsg("既存のプロジェクトを開くには[プロジェクト]メニュー＞[開く]からプロジェクトフォルダを選択してください。");
            projectState.setState(State.NOT_LOADED);
        } else {
            loadProject(pomFile);
            projectState.init(pomFile);
        }

        testToolbarController.initialize(console, messageView, fileTreeController, projectState);
    }

    public void destroy() {
        conversationProcess.destroy();
        testToolbarController.destroy();

        File sampleDir = new File(projectState.getBaseDir(), "sample");
        if (sampleDir.exists()) {
            conversationProcess.start(new TextAreaConsole(console), sampleDir,
                    MavenUtils.getCommand(), "jetty:stop");
        }

    }

    @FXML
    public void createProject() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("プロジェクトを作成するフォルダを選択してください。");
        dirChooser.setInitialDirectory(new File("."));

        File projectDir = dirChooser.showDialog(FxContext.getPrimaryStage());

        if (projectDir == null) {
            return;
        }
        messageView.startMsg("プロジェクトを作成します。");

        File pomFile = projectService.createProject(projectDir);

        if (pomFile == null) {
            messageView.addMsg("プロジェクトは既に存在します。");
            return;
        }

        loadProject(pomFile);
        projectState.init(pomFile);
        messageView.addMsg("プロジェクトを作成しました。");
        messageView.addMsg("[プロジェクト]メニュー＞[サンプルWebサイトを起動]からサンプルを取得・起動することができます。");
    }

    @FXML
    public void openProject() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("プロジェクトのpom.xmlがあるフォルダを選択してください。");
        dirChooser.setInitialDirectory(new File("."));

        File projectDir = dirChooser.showDialog(FxContext.getPrimaryStage());

        if (projectDir == null) {
            return;
        }

        File pomFile = projectService.openProject(projectDir);

        if (pomFile == null) {

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("pom.xmlがありません。作成しますか？");

            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.get() == ButtonType.OK) {
                pomFile = projectService.createProject(projectDir);
                loadProject(pomFile);
            }

        } else {

            loadProject(pomFile);
            messageView.addMsg("プロジェクトを開きました。");

        }
    }

    private void loadProject(File pomFile) {
        fileTreeController.setFileTreeRoot(pomFile.getParentFile());
        projectState.reset();
    }

    @FXML
    public void runSample() {
        projectState.setState(State.LOCKING);
        sampleRunMenu.setVisible(false);
        messageView.startMsg("サンプルWebサイトを起動します。");

        conversationProcess.start(new TextAreaConsole(console), projectState.getBaseDir(),
                SitWtRuntimeUtils.buildSampleCommand());

        conversationProcess.onExit(exitCode -> {

            File sampledir = new File(projectState.getBaseDir(), "sample");
            if (!sampledir.exists()) {
                sampledir.mkdirs();
            }

            JettyConsoleListener listener = new JettyConsoleListener();
            conversationProcess.start(new TextAreaConsole(console, listener), sampledir,
                    MavenUtils.getCommand());

            ExecutorContainer.get().execute(() -> {
                if (listener.isSuccess()) {
                    String sampleBaseUrl = "http://localhost:8280";
                    messageView.addMsg("サンプルWebサイトを起動しました。" + sampleBaseUrl + "/input.html");
                    messageView.addMsg(
                            "サンプルテストスクリプトtestscript/SampleTestScript.xlsxを左のツリーで選択して実行できます。");
                    // TODO URLの動的取得
                    testToolbarController.setBaseUrl(sampleBaseUrl);
                    sampleStopMenu.setVisible(true);
                } else {
                    messageView.addMsg("サンプルWebサイトの起動に失敗しました。");
                    sampleRunMenu.setVisible(true);
                }
                Platform.runLater(() -> fileTreeController.refresh());
                projectState.reset();
            });

        });
    }

    @FXML
    public void stopSample() {
        sampleStopMenu.setVisible(false);
        messageView.startMsg("サンプルWebサイトを停止します。");
        File sampledir = new File(projectState.getBaseDir(), "sample");
        conversationProcess.start(new TextAreaConsole(console), sampledir, MavenUtils.getCommand(),
                "jetty:stop");

        conversationProcess.onExit(exitCode -> {

            Platform.runLater(() -> {
                sampleRunMenu.setVisible(true);
                messageView.addMsg("サンプルWebサイトを停止しました。");
            });
        });
    }

    @FXML
    public void page2script() {
        messageView.startMsg("ブラウザでページを表示した状態で「スクリプト生成」ボタンをクリックしてください。");
        List<String> command = SitWtRuntimeUtils.buildPage2ScriptCommand(
                testToolbarController.getDriverType(), testToolbarController.getBaseUrl());

        conversationProcess.start(textAreaConsole, projectState.getBaseDir(), command);

        projectState.setState(State.BROWSING);
        conversationProcess.onExit(exitCode -> {
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
        messageView.addMsg("Selenium IDEで記録したテストソクリプトをhtml形式でtestcriptディレクトリに保存してください。");
        List<String> command = SitWtRuntimeUtils
                .buildOpe2ScriptCommand(testToolbarController.getBaseUrl());

        conversationProcess.start(textAreaConsole, projectState.getBaseDir(), command);
    }

    @FXML
    public void export() {
        conversationProcess.input("e");
        fileTreeController.refresh();
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

    private double stageHeight;

    private double stageWidth;

    @FXML
    private Button maximizeButton;

    @FXML
    private Button minimizeButton;

    private BooleanProperty windowMaximized = new SimpleBooleanProperty(true);

    @FXML
    public void minimizeWindow() {
        Stage primaryStage = FxContext.getPrimaryStage();
        stageHeight = primaryStage.getHeight();
        stageWidth = primaryStage.getWidth();
        // TODO コンソールのサイズ設定
        StageResizer.resize(primaryStage, 600, 90);
        windowMaximized.set(false);
    }

    @FXML
    public void maximizeWindow() {
        Stage primaryStage = FxContext.getPrimaryStage();
        StageResizer.resize(primaryStage, stageWidth, stageHeight);
        windowMaximized.set(true);
    }

}
