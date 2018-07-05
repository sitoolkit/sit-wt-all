package org.sitoolkit.wt.gui.pres.editor;

import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.event.ActionEvent;

public class TestScriptEditorController{

    final TestScriptEditor testScriptEditor;
    final SpreadsheetView spreadSheet;

    public TestScriptEditorController(TestScriptEditor testScriptEditor, SpreadsheetView spreadSheet) {
        this.testScriptEditor = testScriptEditor;
        this.spreadSheet = spreadSheet;
    }

    public void newTestCase(ActionEvent e) {
        testScriptEditor.addTestCase(spreadSheet);
    }

    public void newTestStep(ActionEvent e) {
        testScriptEditor.addTestStep(spreadSheet);
    }

}
