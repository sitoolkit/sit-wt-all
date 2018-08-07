package org.sitoolkit.wt.gui.infra.log;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaOutputStream extends ByteArrayOutputStream {

    private TextArea textArea;

    public TextAreaOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public synchronized void write(int b) {
        Platform.runLater(() -> {
            super.write(b);

            if (b == 10 && textArea != null) {
                try {
                    textArea.appendText(toString(Charset.defaultCharset().name()));
                    super.reset();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
