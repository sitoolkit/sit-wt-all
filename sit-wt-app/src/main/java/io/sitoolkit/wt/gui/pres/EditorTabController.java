package io.sitoolkit.wt.gui.pres;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.wt.domain.debug.DebugListener;
import io.sitoolkit.wt.gui.app.script.ScriptService;
import io.sitoolkit.wt.gui.domain.test.DebugListenerFinder;
import io.sitoolkit.wt.gui.pres.editor.EditorController;
import io.sitoolkit.wt.gui.pres.editor.TestScriptEditorController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import lombok.Getter;
import lombok.Setter;

public class EditorTabController implements FileOpenable, DebugListenerFinder {

    @Setter
    private TabPane tabs;

    @Getter
    private BooleanProperty empty = new SimpleBooleanProperty(true);

    @Setter
    private ScriptService scriptService;

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
        open(file.toPath());
    }

    public void open(Path file) {
        Optional<Tab> editorTab = findTabByFile(file);

        if (editorTab.isPresent()) {
            SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();
            selectionModel.select(editorTab.get());

        } else {
            EditorController editorContoroller = getEditorController(file);
            editorContoroller.open(file);

            Tab tab = new Tab();
            tab.setUserData(editorContoroller);
            setFileInfo(tab, file);
            tab.setContent(editorContoroller.getEditorContent());
            tabs.getTabs().add(tab);

        }
    }

    public void save() {
        getSelectedEditorController().save();
    }

    public void saveAs(Path file) {
        getSelectedEditorController().saveAs(file);
        setFileInfo(tabs.getSelectionModel().getSelectedItem(), file);
    }

    private Optional<Tab> findTabByFile(Path file) {
        return tabs.getTabs().stream()
                .filter(tab -> tab.getTooltip().getText().equals(file.toAbsolutePath().toString()))
                .findFirst();
    }

    private EditorController getEditorController(Path file) {
        String pathStr = file.toString();
        if (pathStr.endsWith(".csv")) {
            return new TestScriptEditorController(scriptService);
        } else if (pathStr.endsWith(".html")) {

        }
        throw new UnsupportedOperationException("File type is not supported : " + file);
    }

    private EditorController getSelectedEditorController() {
        return (EditorController) tabs.getSelectionModel().getSelectedItem().getUserData();
    }

    private void setFileInfo(Tab tab, Path file) {
        tab.setText(file.getFileName().toString());
        tab.setTooltip(new Tooltip(file.toAbsolutePath().toString()));
    }

    @Override
    public Optional<DebugListener> find(Path path) {
        return tabs.getTabs().stream()
                .filter(tab -> tab.getTooltip().getText().equals(path.toAbsolutePath().toString()))
                .filter(tab -> DebugListener.class.isInstance(tab.getUserData()))
                .map(tab -> DebugListener.class.cast(tab.getUserData())).findFirst();
    }

}
