package org.sitoolkit.wt.gui.infra;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaConsole implements Console {

    private TextArea textArea;

    private ConsoleListener listener;

    public TextAreaConsole(TextArea textArea) {
        super();
        this.textArea = textArea;
    }

    public TextAreaConsole(TextArea textArea, ConsoleListener listener) {
        super();
        this.textArea = textArea;
        this.listener = listener;
    }

    @Override
    public void append(String str) {
        if (listener != null) {
            listener.readLine(str);
        }

        Platform.runLater(() -> textArea.appendText(str + System.lineSeparator()));
    }

}
