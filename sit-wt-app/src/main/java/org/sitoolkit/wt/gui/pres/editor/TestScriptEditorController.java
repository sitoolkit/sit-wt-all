package org.sitoolkit.wt.gui.pres.editor;

import java.util.List;

import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.event.ActionEvent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

public class TestScriptEditorController {

    static final DataFormat DATAFORMAT_SPREADSHEET;

    static {
        DataFormat fmt;
        if ((fmt = DataFormat.lookupMimeType("SpreadsheetView")) == null) {
            fmt = new DataFormat("SpreadsheetView");
        }
        DATAFORMAT_SPREADSHEET = fmt;
    }

    final TestScriptEditor testScriptEditor;
    final SpreadsheetView spreadSheet;

    public TestScriptEditorController(TestScriptEditor testScriptEditor, SpreadsheetView spreadSheet) {
        this.testScriptEditor = testScriptEditor;
        this.spreadSheet = spreadSheet;
    }

    public void newTestCase(ActionEvent e) {
        testScriptEditor.insertTestCase(spreadSheet);
    }

    public void newTestStep(ActionEvent e) {
        testScriptEditor.insertTestStep(spreadSheet);
    }

    public void newTestCaseTail(ActionEvent e) {
        testScriptEditor.appendTestCase(spreadSheet);
    }

    public void newTestStepTail(ActionEvent e) {
        testScriptEditor.appendTestStep(spreadSheet);
    }

    public void pasteCase(ActionEvent e) {

        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

            @SuppressWarnings("unchecked")
            List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
            int count = testScriptEditor.getCaseCount(spreadSheet, changeList);
            if (count > 0) {
                if (testScriptEditor.insertTestCases(spreadSheet, count)) {
                    spreadSheet.pasteClipboard();
                }
            }
        }
    }


    public void pasteStep(ActionEvent e) {

        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

            @SuppressWarnings("unchecked")
            List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
            int count = testScriptEditor.getStepCount(spreadSheet, changeList);
            if (count > 0) {
                if (testScriptEditor.insertTestSteps(spreadSheet, count)) {
                    spreadSheet.pasteClipboard();
                }
            }
        }
    }



}
