package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.domain.MavenConsoleListener;
import org.sitoolkit.wt.gui.domain.ProjectState;
import org.sitoolkit.wt.gui.domain.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.ConversationProcess;
import org.sitoolkit.wt.gui.infra.FileIOUtils;
import org.sitoolkit.wt.gui.infra.FxContext;
import org.sitoolkit.wt.gui.infra.MavenUtils;
import org.sitoolkit.wt.gui.infra.PropertyManager;
import org.sitoolkit.wt.gui.infra.StageResizer;
import org.sitoolkit.wt.gui.infra.StrUtils;
import org.sitoolkit.wt.gui.infra.SystemUtils;
import org.sitoolkit.wt.gui.infra.TextAreaConsole;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AppController implements Initializable {

    private static final String POM_URL = "https://raw.githubusercontent.com/sitoolkit/sit-wt-all/master/distribution/pom.xml";

    @FXML
    private Menu baseUrlMenu;

    @FXML
    private ToolBar startGroup;

    @FXML
    private ToolBar runningGroup;

    @FXML
    private ToolBar debugGroup;

    @FXML
    private TextArea console;

    @FXML
    private Button runButton;

    @FXML
    private CheckBox debugCheck;

    @FXML
    private CheckBox parallelCheck;

    @FXML
    private ChoiceBox<String> browserChoice;

    @FXML
    private ComboBox<String> baseUrlCombo;

    @FXML
    private Button pauseButton;

    @FXML
    private TextField stepNoText;

    @FXML
    private Button quitButton;

    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button exportButton;

    @FXML
    private Button toggleButton;

    @FXML
    private Label statusLabel;

    @FXML
    private FileTreeController fileTreeController;

    private ConversationProcess mvnProcess = new ConversationProcess();

    private ProjectState projectState = new ProjectState();

    private File pomFile = new File("pom.xml");

    private MavenConsoleListener mavenConsoleListener = new MavenConsoleListener();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // setVisible(projectGroup, Bindings.not(projectState.getRunning()));
        setVisible(startGroup, Bindings.and(projectState.getInitialized(),
                Bindings.not(projectState.getRunning())));
        setVisible(runningGroup, projectState.getRunning());
        setVisible(debugGroup,
                Bindings.and(projectState.getRunning(), debugCheck.selectedProperty()));

        parallelCheck.disableProperty().bind(debugCheck.selectedProperty());

        browserChoice.getItems().addAll(SystemUtils.getBrowsers());

        // TODO プロジェクトの初期化判定はpom.xml内にSIT-WTの設定があること
        projectState.getInitialized().setValue(pomFile.exists());
        if (pomFile.exists()) {
            loadProject(pomFile);
            statusLabel.setText("");
        } else {
            statusLabel.setText("[プロジェクト]>[新規作成]からプロジェクトを作成するフォルダを選択してください。");
        }

    }

    public void destroy() {
        PropertyManager.get().setBaseUrls(baseUrlCombo.getItems());
    }

    private void setVisible(Node node, ObservableBooleanValue visible) {
        node.visibleProperty().bind(visible);
        node.managedProperty().bind(visible);
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

        pomFile = new File(projectDir, "pom.xml");
        if (pomFile.exists()) {
            statusLabel.setText("プロジェクトは既に存在します。");
            return;
        }

        // TODO 外部化
        FileIOUtils.download(POM_URL, pomFile);

        projectState.getInitialized().set(pomFile.exists());

        if (pomFile.exists()) {

            loadProject(pomFile);
            statusLabel.setText("プロジェクトを作成しました。");
        }
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

        pomFile = new File(projectDir, "pom.xml");

        if (pomFile.exists()) {

            loadProject(pomFile);
            statusLabel.setText("プロジェクトを開きました。");

        } else {

            statusLabel.setText("pom.xmlの無いフォルダは無効です。");

        }
    }

    private void loadProject(File pomFile) {
        fileTreeController.setFileTreeRoot(pomFile);
        projectState.getInitialized().set(true);
        PropertyManager.get().load(pomFile.getAbsoluteFile().getParentFile());

        List<String> baseUrls = PropertyManager.get().getBaseUrls();
        if (!baseUrls.isEmpty()) {
            baseUrlCombo.getItems().addAll(baseUrls);
            baseUrlCombo.setValue(baseUrls.get(0));
        }

        PropertyManager.get().setClasspath(SitWtRuntimeUtils.getTestRunnerClasspath(pomFile));
    }

    @FXML
    public void getSample() {
        statusLabel.setText("サンプルを取得します。");

        mvnProcess.start(new TextAreaConsole(console), pomFile.getAbsoluteFile().getParentFile(),
                MavenUtils.getCommand(), "sit-wt:sample");

        mvnProcess.waitFor(() -> Platform.runLater(() -> {
            statusLabel.setText("サンプルを取得しました。");
        }));
    }

    @FXML
    public void run() {
        String testedClasses = SitWtRuntimeUtils
                .findTestedClasses(fileTreeController.getSelectedFiles());

        if (StrUtils.isEmpty(testedClasses)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("");
            alert.setContentText("");
            alert.setHeaderText("実行するテストスクリプトを選択してください。");
            alert.show();
            return;
        }

        statusLabel.setText("テストを実行します。");

        String baseUrl = baseUrlCombo.getValue();
        addBaseUrl(baseUrl);

        List<String> command = SitWtRuntimeUtils.buildSingleTestCommand(fileTreeController.getSelectedFiles(),
                debugCheck.isSelected(),
                browserChoice.getSelectionModel().getSelectedItem(), baseUrl);

        mvnProcess.start(new TextAreaConsole(console, mavenConsoleListener),
                pomFile.getAbsoluteFile().getParentFile(), command);

        projectState.getRunning().setValue(true);
        mvnProcess.waitFor(() -> {
            projectState.getRunning().set(false);
            Platform.runLater(() -> statusLabel.setText("テストを終了します。"));
        });
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
        if (mavenConsoleListener.isPausing()) {
            pauseButton.setText("一時停止");

            String stepNo = stepNoText.getText();
            if (!StrUtils.isEmpty(stepNo)) {
                mvnProcess.input("!" + stepNo);
                mvnProcess.input("#" + stepNo);
            }

            mvnProcess.input("s");
        } else {
            pauseButton.setText("再開");
            mvnProcess.input("");
        }
    }

    @FXML
    public void back() {
        mvnProcess.input("b");
    }

    @FXML
    public void forward() {
        mvnProcess.input("f");
    }

    @FXML
    public void export() {
        mvnProcess.input("e");
    }

    @FXML
    public void openScript() {
        mvnProcess.input("o");
    }

    @FXML
    public void quit() {
        mvnProcess.destroy();
        projectState.getRunning().set(false);
    }

    private double stageHeight;

    private double stageWidth;

    @FXML
    public void toggleWindow() {
        Stage primaryStage = FxContext.getPrimaryStage();

        if ("拡大".equals(toggleButton.getText())) {
            StageResizer.resize(primaryStage, stageWidth, stageHeight);
            toggleButton.setText("縮小");

        } else {

            stageHeight = primaryStage.getHeight();
            stageWidth = primaryStage.getWidth();
            // TODO コンソールのサイズ設定
            StageResizer.resize(primaryStage, 600, 90);

            toggleButton.setText("拡大");
        }
    }

}
