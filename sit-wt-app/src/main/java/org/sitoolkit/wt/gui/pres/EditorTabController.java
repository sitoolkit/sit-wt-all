package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.sitoolkit.wt.domain.debug.DebugListener;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.gui.app.script.ScriptService;
import org.sitoolkit.wt.gui.pres.editor.TestScriptEditor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import lombok.Data;
import lombok.Setter;

public class EditorTabController implements FileOpenable, DebugListener {

    @Setter
    TabPane tabs;

    ScriptService scriptService;

    TestScriptEditor testScriptEditor = new TestScriptEditor();

    Map<String, WaitingPoint> waitingMap  = new HashMap<>();

    private BooleanProperty empty = new SimpleBooleanProperty(true);

    public void initialize() {
        tabs.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldVal, newVal) -> {
                    if (newVal == null) {
                        empty.set(true);
                    } else {
                        empty.set(false);
                    }
                });
    }

    @Override
    public void open(File file) {
        Optional<Tab> scriptTab = tabs.getTabs().stream()
                .filter(tab -> tab.getTooltip().getText().equals(file.getAbsolutePath())).findFirst();

        if (scriptTab.isPresent()) {
            SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();
            selectionModel.select(scriptTab.get());

        } else {
            TestScript testScript = scriptService.read(file);
            Tab tab = new Tab(file.getName());
            SpreadsheetView spreadSheet = testScriptEditor.buildSpreadsheet(testScript);
            WaitingPoint wp = waitingMap.get(file.getAbsolutePath());
            if (wp != null) {
                testScriptEditor.setDebugStyle(spreadSheet, wp.stepIndex, wp.caseIndex);
            }
            tab.setContent(spreadSheet);
            tabs.getSelectionModel().select(tab);

            Tooltip tooltip = new Tooltip();
            tooltip.setText(file.getAbsolutePath());
            tab.setTooltip(tooltip);
            tabs.getTabs().add(tab);
        }
    }

    public void save() {
        Optional<Tab> selectedTab = tabs.getTabs().stream().filter(Tab::isSelected).findFirst();

        SpreadsheetView spreadSheet = (SpreadsheetView) selectedTab.get().getContent();
        TestScript testScript = testScriptEditor.buildTestscript(spreadSheet);
        scriptService.write(testScript);
    }

    public BooleanProperty isEmpty() {
        return this.empty;
    }

    @Override
    public void onPause(Path scriptPath, int stepIndex, int caseIndex) {
        waitingMap.put(scriptPath.toAbsolutePath().toString(), new WaitingPoint(stepIndex, caseIndex));
        Optional<Tab> targetTab = getTab(scriptPath);
        targetTab.ifPresent(tab -> {
            tabs.getSelectionModel().select(tab);
            testScriptEditor.setDebugStyle((SpreadsheetView) tab.getContent(), stepIndex, caseIndex);
        });
    }

    @Override
    public void onStepStart(Path scriptPath, int stepIndex, int caseIndex) {
        waitingMap.remove(scriptPath.toAbsolutePath().toString());
        Optional<Tab> targetTab = getTab(scriptPath);
        targetTab.ifPresent(tab -> {
            testScriptEditor.setRunningStyle((SpreadsheetView) tab.getContent(), stepIndex, caseIndex);
        });
    }

    @Override
    public void onCaseEnd(Path scriptPath, int caseIndex) {
        waitingMap.remove(scriptPath.toAbsolutePath().toString());
        Optional<Tab> targetTab = getTab(scriptPath);
        targetTab.ifPresent(tab -> {
            testScriptEditor.removeRunningDebugStyle((SpreadsheetView) tab.getContent());
        });
    }

    @Override
    public void onClose() {
        waitingMap.clear();
        tabs.getTabs().stream().map(Tab::getContent).map(content -> (SpreadsheetView) content)
                .forEach(testScriptEditor::removeRunningDebugStyle);
    }


    private Optional<Tab> getTab(Path scriptPath) {
        String absolutePath = scriptPath.toAbsolutePath().toString();
        return tabs.getTabs().stream()
                .filter(tab -> tab.getTooltip().getText().equals(absolutePath)).findFirst();
    }

    @Data
    class WaitingPoint {
        final int stepIndex;
        final int caseIndex;
    }

}
