package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import io.sitoolkit.wt.domain.testscript.TestStepInputType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class OperationCellType extends NormalStringCellType implements ChangeListener<Object> {

    private List<String> operationNames;
    private BiConsumer<Integer, String> changeCallback;

    public OperationCellType(BiConsumer<Integer, String> changeCallback) {
        List<TestStepInputType> inputTypes = Arrays.asList(TestStepInputType.values());
        this.operationNames = inputTypes.stream().map(TestStepInputType::getOperationName)
                .collect(Collectors.toList());
        this.changeCallback = changeCallback;
    }

    @Override
    public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan,
            String value) {
        SpreadsheetCell cell = super.createCell(row, column, rowSpan, columnSpan, value);
        cell.itemProperty().addListener(this);

        return cell;
    }

    @Override
    public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
        return new SpreadsheetCellEditor.ListEditor<>(view, operationNames);
    }

    @Override
    public void changed(ObservableValue<? extends Object> observable, Object oldValue,
            Object newValue) {

        SpreadsheetCellBase base = (SpreadsheetCellBase) ((ObjectProperty<?>) observable).getBean();
        changeCallback.accept(base.getRow(), (String) newValue);
    }

}