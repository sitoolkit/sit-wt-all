package org.sitoolkit.wt.gui.pres.editor;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class TestScriptEditorController implements Initializable {

    TestScriptEditor testScriptEditor = new TestScriptEditor();
    SpreadsheetView spreadSheet;

    @FXML
    public void newTestCase() {
        testScriptEditor.addTestCase(spreadSheet);
    }

    @FXML
    public void newTestStep() {
        testScriptEditor.addTestStep(spreadSheet);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setView(SpreadsheetView spreadSheet) {
        this.spreadSheet = spreadSheet;
    }

}
