package org.sitoolkit.wt.gui.infra.fx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.gui.app.script.ScriptFileType;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class ScriptDialog {

    FileChooser fileChooser = new FileChooser();

    public ScriptDialog() {
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(getExtensionFilters());
    }

    private List<ExtensionFilter> getExtensionFilters() {
        List<String> allExtentions = Stream.of(ScriptFileType.values())
                .map(ScriptFileType::getExtentions)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        ExtensionFilter allFillter = new ExtensionFilter("all test script", allExtentions);

        List<ExtensionFilter> filters = new ArrayList<>();
        filters.add(allFillter);
        Stream.of(ScriptFileType.values())
                .map(type -> new ExtensionFilter(type.getDescription(), type.getExtentions()))
                .forEachOrdered(filters::add);

        return filters;
    }

    public File showOpenDialog(Window ownerWindow) {
        fileChooser.setTitle("スクリプトファイルを開く");
        File file = fileChooser.showOpenDialog(ownerWindow);
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
        }
        return file;
    }

    public File showSaveDialog(Window ownerWindow) {
        fileChooser.setTitle("スクリプトファイルを別名保存する");
        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
        }
        return file;
    }

    public Optional<ScriptFileType> getSelectedFileType() {
        return getType(fileChooser.getSelectedExtensionFilter());
    }

    private Optional<ScriptFileType> getType(ExtensionFilter extentsionFilter) {
        return Stream.of(ScriptFileType.values())
                .filter(fileType -> StringUtils.equals(fileType.getDescription(), extentsionFilter.getDescription()))
                .findFirst();
    }
}
