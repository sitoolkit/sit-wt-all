package io.sitoolkit.wt.gui.pres.editor;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Optional;
import javafx.scene.Node;

public class DefaultEditorController implements EditorController {

  @Override
  public void open(Path file) {
    try {
      Desktop.getDesktop().open(file.toFile());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public Optional<Node> getEditorContent() {
    return Optional.empty();
  }

  @Override
  public void save() {
    // NOP
  }

  @Override
  public void saveAs(Path file) {
    // NOP
  }

}
