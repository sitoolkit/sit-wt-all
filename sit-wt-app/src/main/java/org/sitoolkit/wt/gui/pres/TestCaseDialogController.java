package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

public class TestCaseDialogController implements Initializable {

    @FXML
    private Label testScriptLabel;

    @FXML
    private FlowPane caseNoFlowPane;

    @FXML
    private Node content;

    TestRunnable testRunnable;

    public TestCaseDialogController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void showSelectDialog(File currentFile, List<String> caseIdList) {

        caseNoFlowPane.getChildren().clear();

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("ケースを指定して実行");
        dialog.setHeaderText("実行するテストケースを選択してください。");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        testScriptLabel.setText(currentFile.getName());

        CheckBoxResultConverter resultConverter = new CheckBoxResultConverter();
        dialog.setResultConverter(resultConverter);

        for (String caseId : caseIdList) {
            CheckBox checkBox = new CheckBox(caseId);
            resultConverter.add(checkBox);
            caseNoFlowPane.getChildren().add(checkBox);
        }

        dialog.getDialogPane().setContent(content);

        Optional<List<String>> result = dialog.showAndWait();
        result.ifPresent(selectedCaseNos -> {
            if (!selectedCaseNos.isEmpty()) {
                testRunnable.runTest(false, false, currentFile, selectedCaseNos);
            }
        });
    }

    public void setTestRunnable(TestRunnable testRunnable) {
        this.testRunnable = testRunnable;
    }

    class CheckBoxResultConverter implements Callback<ButtonType, List<String>> {

        List<CheckBox> checkBoxes = new ArrayList<>();

        void add(CheckBox checkbox) {
            checkBoxes.add(checkbox);
        }

        @Override
        public List<String> call(ButtonType buttonType) {
            List<String> selected = new ArrayList<>();
            if (buttonType == ButtonType.OK) {
                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isSelected()) {
                        selected.add(checkBox.getText());
                    }
                }
            }
            return selected;
        }

    }
}
