package io.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import io.sitoolkit.wt.gui.domain.project.ProjectState;
import io.sitoolkit.wt.gui.infra.fx.FxContext;
import io.sitoolkit.wt.gui.infra.fx.ScriptDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import lombok.Setter;

public class MenuBarController implements Initializable {

  @FXML
  MenuItem saveMenu;

  @FXML
  MenuItem saveAsMenu;

  @FXML
  MenuItem runMenu;

  @FXML
  MenuItem debugMenu;

  @FXML
  MenuItem runParallelMenu;

  @FXML
  MenuItem quitMenu;

  @FXML
  MenuItem pauseMenu;

  @FXML
  MenuItem restartMenu;

  @FXML
  MenuItem backMenu;

  @FXML
  MenuItem forwardMenu;

  @FXML
  MenuItem maskEvidenceMenu;

  @FXML
  MenuItem setBaseEvidenceMenu;

  @FXML
  MenuItem genDiffEvidenceMenu;

  @FXML
  MenuItem ope2scriptMenu;

  @FXML
  MenuItem page2scriptMenu;

  @FXML
  MenuItem exportMenu;

  @FXML
  MenuItem quitBrowsingMenu;

  @FXML
  MenuItem runSampleMenu;

  @FXML
  MenuItem stopSampleMenu;

  @Setter
  AppController appController;

  @Setter
  private EditorTabController editorTabController;

  @Setter
  TestToolbarController testToolbarController;

  @Setter
  DiffEvidenceToolbarController diffEvidenceToolbarController;

  @Setter
  SampleToolbarController sampleToolbarController;

  @Setter
  ProjectState projectState;

  private ScriptDialog scriptDialog = new ScriptDialog();

  @Override
  public void initialize(URL location, ResourceBundle resources) {}

  public void initialize() {
    saveMenu.disableProperty().bind(editorTabController.getEmpty());
    saveAsMenu.disableProperty().bind(editorTabController.getEmpty());

    runMenu.visibleProperty().bind(projectState.isLoaded());
    debugMenu.visibleProperty().bind(projectState.isLoaded());
    runParallelMenu.visibleProperty().bind(projectState.isLoaded());

    quitMenu.visibleProperty().bind(projectState.isRunning());

    pauseMenu.visibleProperty()
        .bind(projectState.isDebugging().and(testToolbarController.getPausing().not()));
    restartMenu.visibleProperty()
        .bind(projectState.isDebugging().and(testToolbarController.getPausing()));
    backMenu.visibleProperty().bind(projectState.isDebugging());
    forwardMenu.visibleProperty().bind(projectState.isDebugging());

    maskEvidenceMenu.visibleProperty().bind(projectState.isLoaded());
    setBaseEvidenceMenu.visibleProperty().bind(projectState.isLoaded());
    genDiffEvidenceMenu.visibleProperty().bind(projectState.isLoaded());

    ope2scriptMenu.visibleProperty().bind(projectState.isLoaded());
    page2scriptMenu.visibleProperty().bind(projectState.isLoaded());

    exportMenu.visibleProperty().bind(projectState.isBrowsing());
    quitBrowsingMenu.visibleProperty().bind(projectState.isBrowsing());

    runSampleMenu.visibleProperty()
        .bind(sampleToolbarController.getRunning().not().and(projectState.isLoaded()));
    stopSampleMenu.visibleProperty()
        .bind(sampleToolbarController.getRunning().and(projectState.isLoaded()));
  }

  @FXML
  public void openProject() {
    appController.openProject();
  }

  @FXML
  public void open() {
    File file = scriptDialog.showOpenDialog(FxContext.getPrimaryStage());
    if (file != null) {
      editorTabController.open(file.toPath());
    }
  }

  @FXML
  public void save() {
    editorTabController.save();
  }

  @FXML
  public void saveAs() {
    File file = scriptDialog.showSaveDialog(FxContext.getPrimaryStage());
    if (file != null) {
      editorTabController.saveAs(file.toPath());
    }
  }

  @FXML
  public void run() {
    testToolbarController.run();
  }

  @FXML
  public void debug() {
    testToolbarController.debug();
  }

  @FXML
  public void runParallel() {
    testToolbarController.runParallel();
  }

  @FXML
  public void quit() {
    testToolbarController.quit();
  }

  @FXML
  public void pause() {
    testToolbarController.pause();
  }

  @FXML
  public void restart() {
    testToolbarController.restart();
  }

  @FXML
  public void back() {
    testToolbarController.back();
  }

  @FXML
  public void forward() {
    testToolbarController.forward();
  }

  @FXML
  public void maskEvidence() {
    diffEvidenceToolbarController.maskEvidence();
  }

  @FXML
  public void setBaseEvidence() {
    diffEvidenceToolbarController.setBaseEvidence();
  }

  @FXML
  public void genDiffEvidence() {
    diffEvidenceToolbarController.genDiffEvidence();
  }

  @FXML
  public void ope2script() {
    appController.ope2script();
  }

  @FXML
  public void page2script() {
    appController.page2script();
  }

  @FXML
  public void export() {
    appController.export();
  }

  @FXML
  public void quitBrowsing() {
    appController.quitBrowsing();
  }

  @FXML
  public void runSample() {
    sampleToolbarController.runSample();
  }

  @FXML
  public void stopSample() {
    sampleToolbarController.stopSample();
  }

  @FXML
  public void settings() {
    appController.settings();
  }

  @FXML
  public void help() {
    appController.help();
  }
}
