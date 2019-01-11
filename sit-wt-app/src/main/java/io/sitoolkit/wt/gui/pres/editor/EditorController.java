package io.sitoolkit.wt.gui.pres.editor;

import java.nio.file.Path;

import javafx.scene.Node;

public interface EditorController {

    void open(Path file);

    Node getEditorContent();

    void save();

    void saveAs(Path file);

}
