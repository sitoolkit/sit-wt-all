package org.sitoolkit.wt.gui.pres.editor;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.sitoolkit.wt.domain.testscript.TestScript;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextInputDialog;

public class TestScriptEditorController implements Initializable {

    TestScriptEditor testScriptEditor = new TestScriptEditor();
    SpreadsheetView spreadSheet;

    @FXML
    public void newTestCase() {
        //TODO テストケースのデフォルト値仕様確認 テストケース番号 MAX+1 が良いのではないか

        int caseCount = testScriptEditor.getTestCaseCount(spreadSheet);
        TextInputDialog dialog = new TextInputDialog(String.format("%s%03d", (new TestScript()).getCaseNoPrefix(),
                caseCount + 1));
        dialog.setTitle("新規テストケース");
        dialog.setHeaderText("テストケースの名前を入力してください。");
        Optional<String> testCaseName = dialog.showAndWait();

        testScriptEditor.addTestCase(spreadSheet, testCaseName);

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
