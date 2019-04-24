package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.List;
import java.util.function.BiConsumer;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class OperationCellType extends DefaultStringCellType implements ChangeListener<Object> {

    private List<String> items;
    private BiConsumer<Integer, String> changeCallback;

    public OperationCellType(List<String> items, BiConsumer<Integer, String> changeCallback) {
        this.items = items;
        this.changeCallback = changeCallback;
    }

    @Override
    public SpreadsheetCell createCell(int row, int column, String value) {
        SpreadsheetCell cell = super.createCell(row, column, value);
        cell.itemProperty().addListener(this);

        return cell;
    }

    @Override
    public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
        return new SpreadsheetCellEditor.ListEditor<>(view, items);
    }

    @Override
    public void changed(ObservableValue<? extends Object> observable, Object oldValue,
            Object newValue) {

        SpreadsheetCellBase base = (SpreadsheetCellBase) ((ObjectProperty<?>) observable).getBean();
        changeCallback.accept(base.getRow(), (String) newValue);
    }

}