package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.app.script.ScriptService;
import org.sitoolkit.wt.gui.app.test.TestService;
import org.sitoolkit.wt.gui.infra.fx.FileSystemWatchService;
import org.sitoolkit.wt.gui.infra.fx.FileTreeItem;
import org.sitoolkit.wt.gui.infra.fx.FileWrapper;
import org.sitoolkit.wt.gui.infra.fx.FxContext;
import org.sitoolkit.wt.gui.infra.util.StrUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;

public class FileTreeController implements Initializable {

    @FXML
    private TreeView<FileWrapper> fileTree;

    @FXML
    private ContextMenu contextMenu;

    @FXML
    private MenuItem executeMenuItem;

    @FXML
    private MenuItem executeCaseMenuItem;

    private Mode mode = Mode.NORMAL;

    TestService testService = new TestService();

    FileSystemWatchService fileSystemWatchService = new FileSystemWatchService();

    TestRunnable testRunnable;

    ScriptService scriptService = new ScriptService();

    TestCaseDialogController testCaseDialogController;

    public FileTreeController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileTree.setEditable(true);
        fileTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (mode == Mode.CHECKBOX) {
            fileTree.setCellFactory(CheckBoxTreeCell.forTreeView());
        }
        fileTree.setShowRoot(false);

        contextMenu.setOnShowing(value -> {
            operateSelectedItem(selectedItem -> {

                String selectedFileName = selectedItem.getValue().getFile().getName();

                executeMenuItem.setVisible(
                        StrUtils.endsWithAny(selectedFileName, ".xlsx", ".xls", ".csv"));
                executeCaseMenuItem
                        .setVisible(StrUtils.endsWithAny(selectedFileName, ".xlsx", ".xls"));
            });
        });

        fileSystemWatchService.init();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TestCaseDialog.fxml"));
        try {
            loader.load();
            testCaseDialogController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        fileSystemWatchService.destroy();
    }

    public File getRoot() {
        return fileTree.getRoot().getValue().getFile();
    }

    public void setFileTreeRoot(File baseDir) {

        TreeItem<FileWrapper> root = new TreeItem<>();
        root.setValue(new FileWrapper(baseDir));

        FileTreeItem pagescriptItem = new FileTreeItem(newDir(baseDir, "pagescript"));
        pagescriptItem.buildChildren();
        root.getChildren().add(pagescriptItem);
        FileTreeItem testscriptItem = new FileTreeItem(newDir(baseDir, "testscript"));
        testscriptItem.buildChildren();
        root.getChildren().add(testscriptItem);
        FileTreeItem baseEvidenceItem = new FileTreeItem(newDir(baseDir, "base-evidence"));
        baseEvidenceItem.buildChildren();
        root.getChildren().add(baseEvidenceItem);
        FileTreeItem evidenceItem = new FileTreeItem(newDir(baseDir, "evidence"));
        evidenceItem.buildChildren();
        root.getChildren().add(evidenceItem);

        fileTree.setRoot(root);

        fileSystemWatchService.register(pagescriptItem);
        fileSystemWatchService.register(testscriptItem);
        fileSystemWatchService.register(baseEvidenceItem);
        fileSystemWatchService.register(evidenceItem);
    }

    private File newDir(File baseDir, String dir) {
        File newDir = new File(baseDir, dir);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        return newDir;
    }

    public List<File> getSelectedItems(boolean recursive) {
        return tree2files(fileTree.getSelectionModel().getSelectedItems(), recursive);
    }

    public File getSelectedItem() {
        TreeItem<FileWrapper> selectedItem = fileTree.getSelectionModel().getSelectedItem();
        return selectedItem == null ? null : selectedItem.getValue().getFile();
    }

    private List<File> tree2files(List<?> fileTree, boolean recursive) {
        List<File> allFiles = new ArrayList<>();

        for (Object item : fileTree) {
            if (item instanceof FileTreeItem) {
                FileTreeItem casted = (FileTreeItem) item;
                File target = casted.getValue().getFile();

                if (recursive && target.isDirectory()) {
                    allFiles.addAll(tree2files(casted.getChildren(), recursive));
                } else {
                    allFiles.add(target);
                }
            }
        }

        return allFiles;
    }

    @FXML
    public void open() {
        operateSelectedItem(selectedItem -> {
            FxContext.openFile(selectedItem.getValue().getFile());
        });
    }

    @FXML
    public void newFolder() {
        operateSelectedDir(selectedItem -> {

            TextInputDialog dialog = new TextInputDialog("新しいフォルダー");
            dialog.setTitle("新規フォルダー");
            dialog.setHeaderText("フォルダーの名前を入力してください。");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                File newDir = new File(selectedItem.getValue().getFile(), name);

                if (newDir.exists()) {
                    return;
                }

                newDir.mkdir();
            });

        });
    }

    @FXML
    public void newScript() {
        operateSelectedDir(selectedItem -> {
            TextInputDialog dialog = new TextInputDialog("NewTestScript.xlsx");
            dialog.setTitle("新規テストスクリプト");
            dialog.setHeaderText("テストスクリプトの名前を入力してください。");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                name = name.endsWith(".xlsx") ? name : name + ".xlsx";
                File newTestScript = new File(selectedItem.getValue().getFile(), name);

                if (newTestScript.exists()) {
                    // TODO ファイル名重複
                    return;
                }

                testService.createNewScript(fileTree.getRoot().getValue().getFile(), newTestScript);
            });

        });
    }

    @FXML
    public void rename() {
        operateSelectedItem(selectedItem -> {
            File currentFile = selectedItem.getValue().getFile();

            TextInputDialog dialog = new TextInputDialog(currentFile.getName());
            dialog.setTitle("名称変更");
            dialog.setHeaderText("新しい名前を入力してください。");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                File newFile = new File(currentFile.getParent(), name);

                if (newFile.exists()) {
                    return;
                }
                currentFile.renameTo(newFile);
            });
        });
    }

    @FXML
    public void delete() {
        TreeItem<FileWrapper> selectedItem = fileTree.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        selectedItem.getValue().getFile().delete();
        selectedItem.getParent().getChildren().remove(selectedItem);
    }

    @FXML
    public void execute() {
        testRunnable.run();
    }

    @FXML
    public void executeCase() {
        operateSelectedItem(selectedItem -> {
            File selectedFile = selectedItem.getValue().getFile();

            scriptService.readCaseNo(selectedFile, caseNos -> {
                Platform.runLater(
                        () -> testCaseDialogController.showSelectDialog(selectedFile, caseNos));
            });

        });
    }

    private void operateSelectedItem(Operation operation) {
        TreeItem<FileWrapper> selectedItem = fileTree.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        operation.operate(selectedItem);
    }

    private void operateSelectedDir(Operation operation) {
        operateSelectedItem(selectedItem -> {
            File dir = selectedItem.getValue().getFile();
            if (dir.isFile()) {
                dir = dir.getParentFile();
                selectedItem = selectedItem.getParent();
            }
            operation.operate(selectedItem);
        });
    }

    public void setTestRunnable(TestRunnable testRunnable) {
        this.testRunnable = testRunnable;
        this.testCaseDialogController.setTestRunnable(testRunnable);
    }

    public void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("");
        alert.setContentText("");
        alert.setHeaderText(message);
        alert.show();
    }

    @FunctionalInterface
    interface Operation {
        void operate(TreeItem<FileWrapper> selectedItem);
    }

    enum Mode {
        NORMAL, CHECKBOX
    }

}
