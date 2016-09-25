package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.domain.MavenConsoleListener;
import org.sitoolkit.wt.gui.domain.ProjectState;
import org.sitoolkit.wt.gui.domain.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.CheckBoxFileTreeItem;
import org.sitoolkit.wt.gui.infra.ConversationProcess;
import org.sitoolkit.wt.gui.infra.FileWrapper;
import org.sitoolkit.wt.gui.infra.FxContext;
import org.sitoolkit.wt.gui.infra.MavenUtils;
import org.sitoolkit.wt.gui.infra.TextAreaConsole;
import org.sitoolkit.wt.gui.infra.UnExpectedException;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AppController implements Initializable {

    @FXML
    private ToolBar projectGroup;

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
    private Button pauseButton;

    @FXML
    private Button quitButton;

    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button exportButton;

    @FXML
    private ToggleButton logButton;

    @FXML
    private TreeView<FileWrapper> fileTree;

    @FXML
    private Label statusLabel;

    private ConversationProcess mvnProcess = new ConversationProcess();

    private ProjectState projectState = new ProjectState();

    private File pomFile = new File("pom.xml");

    private MavenConsoleListener mavenConsoleListener = new MavenConsoleListener();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setVisible(projectGroup, Bindings.not(projectState.getRunning()));
        setVisible(startGroup, Bindings.and(projectState.getInitialized(),
                Bindings.not(projectState.getRunning())));
        setVisible(runningGroup, projectState.getRunning());
        setVisible(debugGroup,
                Bindings.and(projectState.getRunning(), debugCheck.selectedProperty()));

        initFileTree();

        parallelCheck.disableProperty().bind(debugCheck.selectedProperty());

        // TODO プロジェクトの初期化判定はpom.xml内にSIT-WTの設定があること
        projectState.getInitialized().setValue(pomFile.exists());
        if (pomFile.exists()) {
            FxContext.setTitie(pomFile.getAbsoluteFile().getParentFile().getAbsolutePath());
            statusLabel.setText("");
        } else {
            statusLabel.setText("[プロジェクト]>[新規作成]からプロジェクトを作成するフォルダを選択してください。");
        }

    }

    private void setVisible(Node node, ObservableBooleanValue visible) {
        node.visibleProperty().bind(visible);
        node.managedProperty().bind(visible);
    }

    private void initFileTree() {
        fileTree.setEditable(true);
        fileTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileTree.setCellFactory(CheckBoxTreeCell.<FileWrapper> forTreeView());
        fileTree.setShowRoot(true);
    }

    private void setFileTreeRoot() {
        File testscriptDir = new File(pomFile.getParent(), "testscript");
        if (!testscriptDir.exists()) {
            testscriptDir.mkdirs();
        }

        fileTree.setRoot(new CheckBoxFileTreeItem(testscriptDir));
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
        String pomUrl = "https://raw.githubusercontent.com/sitoolkit/sit-wt-all/master/distribution/pom.xml";
        try (InputStream pom = new URL(pomUrl).openStream()) {
            Files.copy(pom, pomFile.toPath());
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }

        projectState.getInitialized().set(pomFile.exists());
        if (pomFile.exists()) {
            setFileTreeRoot();
            FxContext.setTitie(pomFile.getParentFile().getAbsolutePath());
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
            setFileTreeRoot();
            projectState.getInitialized().set(true);
            statusLabel.setText("プロジェクトを開きました。");
        } else {
            statusLabel.setText("pom.xmlの無いフォルダは無効です。");
        }
    }

    @FXML
    public void getSample() {
        statusLabel.setText("サンプルを取得します。");

        mvnProcess.start(new TextAreaConsole(console), pomFile.getAbsoluteFile().getParentFile(),
                MavenUtils.getCommand(), "sit-wt:sample");

        mvnProcess.waitFor(() -> Platform.runLater(() -> {
            fileTree.refresh();
            statusLabel.setText("サンプルを取得しました。");
        }));
    }

    @FXML
    public void run() {
        statusLabel.setText("テストを実行します。");

        List<String> command = SitWtRuntimeUtils.buildCommand(
                ((CheckBoxFileTreeItem) fileTree.getRoot()).getSelectedFiles(),
                debugCheck.isSelected(), !parallelCheck.isDisabled() && parallelCheck.isSelected(),
                browserChoice.getSelectionModel().getSelectedItem());

        mvnProcess.start(new TextAreaConsole(console, mavenConsoleListener),
                pomFile.getAbsoluteFile().getParentFile(), command);

        projectState.getRunning().setValue(true);
        mvnProcess.waitFor(() -> {
            projectState.getRunning().set(false);
            Platform.runLater(() -> statusLabel.setText("テストを終了します。"));
        });
    }

    @FXML
    public void pause() {
        if (mavenConsoleListener.isPausing()) {
            pauseButton.setText("一時停止");
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

    @FXML
    public void toggleConsole() {
        Stage primaryStage = FxContext.getPrimaryStage();
        if (logButton.isSelected()) {
            // TODO コンソールのサイズ設定
            primaryStage.setHeight(400);
        } else {
            console.setPrefHeight(0);
            primaryStage.setHeight(primaryStage.getMinHeight());
        }
    }

}
