
package io.sitoolkit.wt.util.infra.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.scene.control.ChoiceDialog;

/**
 * このクラスは、ファイルを上書き可否をユーザーに確認するためのクラスです。
 */
public class OverwriteChecker {

  private Writable allWritable = Writable.NA;

  private boolean rebuild;

  public boolean isWritable(Path path) {
    if (!Files.exists(path)) {
      return true;
    }

    if (rebuild) {
      return true;
    }

    if (Writable.No.equals(allWritable)) {
      return false;
    } else if (Writable.Yes.equals(allWritable)) {
      return true;
    }

    Answer answer = confirmOverwriteInFxApplicationThread(path);
    allWritable = answer.allWritable;
    return Writable.Yes.equals(answer.writable);
  }

  private Answer confirmOverwriteInFxApplicationThread(Path path) {
    if (Platform.isFxApplicationThread()) {
      return confirmOverwrite(path);
    }

    FutureTask<Answer> task = new FutureTask<>(() -> {
      return confirmOverwrite(path);
    });
    Platform.runLater(task);

    try {
      return task.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private Answer confirmOverwrite(Path path) {
    ChoiceDialog<Answer> choice = new ChoiceDialog<>(Answer.n, Answer.values());
    choice.setHeaderText("SIToolkit ファイル上書き確認");
    choice.setContentText("書込み先にファイルが存在します。\n" + path.toAbsolutePath());
    return choice.showAndWait().orElse(Answer.n);
  }

  enum Writable {
    Yes, No, NA
  }

  enum Answer {

    //@formatter:off
    y("上書き", Writable.Yes, Writable.NA),
    a("以降全て上書き", Writable.Yes, Writable.Yes),
    n("上書きしない", Writable.No, Writable.NA),
    q("以降全て上書きしない", Writable.No, Writable.NA),
    ;
    //@formatter:on

    final String description;
    final Writable writable;
    final Writable allWritable;

    private Answer(String description, Writable writable, Writable allWritable) {
      this.description = description;
      this.writable = writable;
      this.allWritable = allWritable;
    }

    @Override
    public String toString() {
      return description;
    }
  }

  public boolean isRebuild() {
    return rebuild;
  }

  public void setRebuild(boolean rebuild) {
    this.rebuild = rebuild;
  }

}
