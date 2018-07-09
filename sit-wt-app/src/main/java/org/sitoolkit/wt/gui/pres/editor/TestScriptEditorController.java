package org.sitoolkit.wt.gui.pres.editor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.event.ActionEvent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

public class TestScriptEditorController {

    static final DataFormat DATAFORMAT_CASES = new DataFormat("sit-wt-testscript-cases");
    static final DataFormat DATAFORMAT_STEPS = new DataFormat("sit-wt-testscript-steps");

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

    public void copyCase(ActionEvent e) {

        List<List<String>> selectedCases = testScriptEditor.getSelectedCases(spreadSheet);
        if (selectedCases.isEmpty()) {
            return;
        }

        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.put(DATAFORMAT_CASES, selectedCases);

        String stringFormatted = IntStream.range(0, selectedCases.get(0).size())
                .mapToObj(
                        rowNum -> selectedCases.stream().map(
                                testCase -> testCase.get(rowNum)).collect(Collectors.joining("\t")))
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));

        content.putString(stringFormatted);
        cb.setContent(content);

    }

    public void copyStep(ActionEvent e) {
        List<List<String>> selectedSteps = testScriptEditor.getSelectedSteps(spreadSheet);
        if (selectedSteps.isEmpty()) {
            return;
        }

        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.put(DATAFORMAT_STEPS, selectedSteps);
        String stringFormatted = selectedSteps.stream()
                .map(strList -> String.join("\t", strList))
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
        content.putString(stringFormatted);
        cb.setContent(content);
    }

    public void pasteCase(ActionEvent e) {

        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_CASES)) {
            @SuppressWarnings("unchecked")
            List<List<String>> cases = (List<List<String>>) cb.getContent(DATAFORMAT_CASES);
            testScriptEditor.pasteCases(spreadSheet, cases);
        }

    }

    public void pasteStep(ActionEvent e) {

        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_STEPS)) {
            @SuppressWarnings("unchecked")
            List<List<String>> steps = (List<List<String>>) cb.getContent(DATAFORMAT_STEPS);
            testScriptEditor.pasteSteps(spreadSheet, steps);
        }
    }
}
