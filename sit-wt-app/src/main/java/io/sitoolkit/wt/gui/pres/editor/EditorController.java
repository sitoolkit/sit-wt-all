package io.sitoolkit.wt.gui.pres.editor;

import java.nio.file.Path;
import java.util.Optional;
import javafx.scene.Node;

public interface EditorController {

  void open(Path file);

  Optional<Node> getEditorContent();

  void save();

  void saveAs(Path file);

}
