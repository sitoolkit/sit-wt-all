package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.List;
import java.util.function.BiConsumer;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class OperationCellType extends SpreadsheetCellType.ListType
        implements ChangeListener<Object> {

    private BiConsumer<Integer, String> changeConsumer;

    public OperationCellType(List<String> items, BiConsumer<Integer, String> changeConsumer) {
        super(items);
        this.changeConsumer = changeConsumer;
    }

    @Override
    public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan,
            String value) {
        SpreadsheetCell cell = super.createCell(row, column, rowSpan, columnSpan, value);
        cell.itemProperty().addListener(this);

        return cell;
    }

    @Override
    public void changed(ObservableValue<? extends Object> observable, Object oldValue,
            Object newValue) {
        SpreadsheetCellBase base = (SpreadsheetCellBase) ((ObjectProperty<?>) observable).getBean();

        changeConsumer.accept(base.getRow(), (String) newValue);
    }

}