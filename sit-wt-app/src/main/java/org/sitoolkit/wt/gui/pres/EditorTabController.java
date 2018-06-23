package org.sitoolkit.wt.gui.pres;

import java.io.File;
import java.util.Optional;

import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.gui.app.script.ScriptService;
import org.sitoolkit.wt.gui.pres.editor.TestScriptEditor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import lombok.Setter;

public class EditorTabController implements FileOpenable {

    @Setter
    TabPane tabs;

    ScriptService scriptService;

    TestScriptEditor testScriptEditor = new TestScriptEditor();

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
        TestScript testScript = scriptService.read(file);
        Tab tab = new Tab(file.getName());
        SpreadsheetView spreadSheet = testScriptEditor.buildSpreadsheet(testScript);
        tab.setContent(spreadSheet);

        Tooltip tooltip = new Tooltip();
        tooltip.setText(file.getAbsolutePath());
        tab.setTooltip(tooltip);
        tabs.getTabs().add(tab);
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
}
