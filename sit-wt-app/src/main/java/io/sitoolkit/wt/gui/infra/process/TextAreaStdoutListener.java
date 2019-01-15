package io.sitoolkit.wt.gui.infra.process;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaStdoutListener implements StdoutListener {

    private TextArea textArea;

    public TextAreaStdoutListener() {
    }

    public TextAreaStdoutListener(TextArea textArea) {
        super();
        this.textArea = textArea;
    }

    @Override
    public void nextLine(String line) {
        Platform.runLater(() -> textArea.appendText(line + System.lineSeparator()));
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

}
