package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.domain.MavenConsoleListener;
import org.sitoolkit.wt.gui.domain.ProjectState;
import org.sitoolkit.wt.gui.infra.ConversationProcess;
import org.sitoolkit.wt.gui.infra.FxContext;
import org.sitoolkit.wt.gui.infra.MavenUtils;
import org.sitoolkit.wt.gui.infra.TextAreaConsole;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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

        // TODO プロジェクトの初期化判定はpom.xml内にSIT-WTの設定があること
        projectState.getInitialized().setValue(pomFile.exists());
        if (pomFile.exists()) {
            FxContext.setTitie(pomFile.getAbsoluteFile().getParentFile().getAbsolutePath());
        }

        setVisible(console, logButton.selectedProperty());
    }

    private void setVisible(Node node, ObservableBooleanValue visible) {
        node.visibleProperty().bind(visible);
        node.managedProperty().bind(visible);
    }

    @FXML
    public void createProject() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File projectDir = dirChooser.showDialog(FxContext.getPrimaryStage());

        if (projectDir == null) {
            return;
        }

        pomFile = new File(projectDir, "pom.xml");

        // TODO 外部化
        String pomUrl = "https://raw.githubusercontent.com/sitoolkit/sit-wt-all/master/distribution/pom.xml";
        try (InputStream pom = new URL(pomUrl).openStream()) {
            Files.copy(pom, pomFile.toPath());
        } catch (IOException e) {
            // TODO 例外処理
            e.printStackTrace();
        }

        projectState.getInitialized().set(pomFile.exists());
        if (pomFile.exists()) {
            FxContext.setTitie(pomFile.getParentFile().getAbsolutePath());
        }
    }

    @FXML
    public void openProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("*.xml", "*.xml"));
        pomFile = fileChooser.showOpenDialog(FxContext.getPrimaryStage());

        projectState.getInitialized().set(pomFile != null && pomFile.exists());
    }

    @FXML
    public void getSample() {
        mvnProcess.start(new TextAreaConsole(console), pomFile.getAbsoluteFile().getParentFile(),
                MavenUtils.getCommand(), "sit-wt:sample");
    }

    @FXML
    public void run() {

        List<String> command = new ArrayList<>();
        command.add(MavenUtils.getCommand());
        command.add("clean");
        command.add("verify");

        List<String> profiles = new ArrayList<>();
        if (debugCheck.isSelected()) {
            profiles.add("debug");
        }
        if (parallelCheck.isSelected()) {
            profiles.add("parallel");
        }
        if (!profiles.isEmpty()) {
            command.add("-P" + String.join(",", profiles));
        }

        mvnProcess.start(new TextAreaConsole(console, mavenConsoleListener),
                pomFile.getAbsoluteFile().getParentFile(), command);

        projectState.getRunning().setValue(true);
        mvnProcess.waitFor(() -> projectState.getRunning().set(false));
    }

    @FXML
    public void pause() {
        if (mavenConsoleListener.isPausing()) {
            mvnProcess.input("s");
        } else {
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
            primaryStage.setHeight(400);
            primaryStage.setResizable(true);
        } else {
            primaryStage.setHeight(primaryStage.getMinHeight());
            primaryStage.setResizable(false);
        }
    }

}
