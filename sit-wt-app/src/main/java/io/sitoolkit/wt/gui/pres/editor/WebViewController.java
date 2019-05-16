package io.sitoolkit.wt.gui.pres.editor;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.web.WebView;

public class WebViewController implements EditorController {

  private WebView webView = new WebView();

  @Override
  public void open(Path file) {
    try {
      webView.getEngine().load(file.toUri().toURL().toString());
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public Optional<Node> getEditorContent() {
    return Optional.of(webView);
  }

  @Override
  public void save() {}

  @Override
  public void saveAs(Path file) {}

}
