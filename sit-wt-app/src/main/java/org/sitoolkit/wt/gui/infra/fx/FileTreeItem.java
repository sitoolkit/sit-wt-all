package org.sitoolkit.wt.gui.infra.fx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public final class FileTreeItem extends CheckBoxTreeItem<FileWrapper> {
    private boolean isLeaf;
    private boolean isFirstTimeLeaf = true;
    private boolean isSelectable = true;

    public FileTreeItem(File file) {
        super(new FileWrapper(file));

        String url = file.isDirectory() ? "/icon/ic_folder_open_black_18dp_1x.png"
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
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            FileWrapper file = getValue();
            isLeaf = file.getFile().isFile();
        }

        return isLeaf;
    }

    public void buildChildren() {
        FileWrapper file = getValue();

        if (file != null && file.getFile().isDirectory()) {

            File[] files = file.getFile().listFiles();

            if (files != null) {

                for (File childFile : files) {
                    addChild(childFile);
                }

            }
        }
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public FileTreeItem addChild(File childFile) {
        FileTreeItem child = new FileTreeItem(childFile);
        child.isSelectable = isSelectable;
        child.buildChildren();
        getChildren().add(child);
        return child;
    }

}