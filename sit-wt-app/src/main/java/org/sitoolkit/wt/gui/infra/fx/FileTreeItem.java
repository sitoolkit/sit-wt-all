package org.sitoolkit.wt.gui.infra.fx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public final class FileTreeItem extends CheckBoxTreeItem<FileWrapper> {
    private boolean isLeaf;
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;
    private boolean isSelectable = true;

    public FileTreeItem(File file) {
        this(new FileWrapper(file));

    }

    public FileTreeItem(File file, boolean isSelectable) {
        this(new FileWrapper(file));
        this.isSelectable = isSelectable;
    }

    public FileTreeItem(FileWrapper file) {
        super(file);

        String url = file.getFile().isDirectory() ? "/icon/ic_folder_open_black_18dp_1x.png"
                : "/icon/ic_description_black_18dp_1x.png";
        setGraphic(new ImageView(url));
    }

    public List<File> getSelectedFiles() {
        List<File> selectedFiles = new ArrayList<>();

        if (isSelected()) {
            selectedFiles.add(getValue().getFile());
        }

        for (TreeItem<FileWrapper> child : getChildren()) {
            selectedFiles.addAll(((FileTreeItem) child).getSelectedFiles());
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
                    FileTreeItem child = new FileTreeItem(new FileWrapper(childFile));
                    child.isSelectable = this.isSelectable;
                    children.add(child);
                }

                return children;
            }
        }

        return FXCollections.emptyObservableList();
    }

    public boolean isSelectable() {
        return isSelectable;
    }

}