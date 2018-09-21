package io.sitoolkit.wt.gui.pres;

import io.sitoolkit.wt.util.infra.util.StrUtils;
import javafx.scene.control.TextArea;

public class MessageView {

	private TextArea textArea;

	public void startMsg(String msg) {
		if (StrUtils.isNotEmpty(textArea.getText())) {
			textArea.appendText(System.lineSeparator());
			textArea.appendText(System.lineSeparator());
			textArea.appendText(System.lineSeparator());
		}
		addMsg(msg);
	}

	public void addMsg(String msg) {
		textArea.appendText(msg);
		textArea.appendText(System.lineSeparator());
	}

	public void setTextArea(TextArea textArea) {
		this.textArea = textArea;
	}

}
