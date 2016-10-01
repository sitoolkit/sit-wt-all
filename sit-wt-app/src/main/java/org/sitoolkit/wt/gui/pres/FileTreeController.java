package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.sitoolkit.wt.gui.infra.CheckBoxFileTreeItem;
import org.sitoolkit.wt.gui.infra.FileIOUtils;
import org.sitoolkit.wt.gui.infra.FileWrapper;
import org.sitoolkit.wt.gui.infra.FxContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;

public class FileTreeController implements Initializable {

    private static final String TESTSCRIPT_URL = "https://github.com/sitoolkit/sit-wt-all/blob/master/sit-wt-runtime/src/main/resources/TestScriptTemplate.xlsx?raw=true";

    @FXML
    private TreeView<FileWrapper> fileTree;

    public FileTreeController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileTree.setEditable(true);
        fileTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileTree.setCellFactory(CheckBoxTreeCell.forTreeView());
        fileTree.setShowRoot(false);

    }

    public void setFileTreeRoot(File pomFile) {
        String baseDir = pomFile.getParent();

        TreeItem<FileWrapper> root = new TreeItem<>();
        root.getChildren().add(new CheckBoxFileTreeItem(newDir(baseDir, "seleniumscript")));
        // TODO pageobjディレクトリの選択を不可にする
        root.getChildren().add(new CheckBoxFileTreeItem(newDir(baseDir, "pageobj"), false));
        root.getChildren().add(new CheckBoxFileTreeItem(newDir(baseDir, "testscript")));

        fileTree.setRoot(root);
    }

    private File newDir(String baseDir, String dir) {
        File newDir = new File(baseDir, dir);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        return newDir;
    }

    public List<File> getSelectedFiles() {
        List<File> selectedFiles = new ArrayList<>();

        for (TreeItem<?> item : fileTree.getRoot().getChildren()) {
            CheckBoxFileTreeItem casted = (CheckBoxFileTreeItem) item;
            if (casted.isSelectable()) {
                selectedFiles.addAll(casted.getSelectedFiles());
            }
        }

        return selectedFiles;
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
                selectedItem.getChildren().add(new CheckBoxFileTreeItem(newDir));
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
                    return;
                }

                FileIOUtils.download(TESTSCRIPT_URL, newTestScript);
                selectedItem.getChildren().add(new CheckBoxFileTreeItem(newTestScript));
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
                selectedItem.setValue(new FileWrapper(newFile));
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

    @FunctionalInterface
    public interface Operation {
        void operate(TreeItem<FileWrapper> selectedItem);
    }

}
