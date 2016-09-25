package org.sitoolkit.wt.gui.infra;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

public final class CheckBoxFileTreeItem extends CheckBoxTreeItem<FileWrapper> {
    private boolean isLeaf;
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;

    public CheckBoxFileTreeItem(File file) {
        this(new FileWrapper(file));
    }

    public CheckBoxFileTreeItem(FileWrapper file) {
        super(file);
    }

    public List<File> getSelectedFiles() {
        List<File> selectedFiles = new ArrayList<>();

        if (isSelected()) {
            selectedFiles.add(getValue().getFile());
        }

        for (TreeItem<FileWrapper> child : getChildren()) {
            selectedFiles.addAll(((CheckBoxFileTreeItem) child).getSelectedFiles());
        }

        return selectedFiles;
    }

    @Override
    public ObservableList<TreeItem<FileWrapper>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;

            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            FileWrapper file = getValue();
            isLeaf = file.getFile().isFile();
        }

        return isLeaf;
    }

    private ObservableList<TreeItem<FileWrapper>> buildChildren(TreeItem<FileWrapper> TreeItem) {
        FileWrapper file = TreeItem.getValue();
        if (file != null && file.getFile().isDirectory()) {
            File[] files = file.getFile().listFiles();
            if (files != null) {
                ObservableList<TreeItem<FileWrapper>> children = FXCollections
                        .observableArrayList();

                for (File childFile : files) {
                    children.add(this.createNode(new FileWrapper(childFile)));
                }

                return children;
            }
        }

        return FXCollections.emptyObservableList();
    }

    private TreeItem<FileWrapper> createNode(FileWrapper f) {
        return new CheckBoxFileTreeItem(f);
    }

}