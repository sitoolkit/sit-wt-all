package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.util.converter.DefaultStringConverter;

public class OkCancelDataCellType extends SpreadsheetCellType.StringType {

    private static final String BLANK = "";
    private static final String OK = "ok";
    private static final String CANCEL = "cancel";

    private static final Pattern OK_PATTERN = Pattern.compile("(ok|true|y)",
            Pattern.CASE_INSENSITIVE);

    private static List<String> items = Arrays.asList(BLANK, OK, CANCEL);

    public OkCancelDataCellType() {
        super(new DefaultStringConverter() {
            @Override
            public String fromString(String value) {
                if (StringUtils.isBlank(value)) {
                    return BLANK;
                }

                Matcher matcher = OK_PATTERN.matcher(value.toString());
                return matcher.find() ? OK : CANCEL;
            }
        });
    }

    @Override
    public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan,
            String value) {
        return super.createCell(row, column, rowSpan, columnSpan, convertValue(value));
    }

    @Override
    public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
        return new SpreadsheetCellEditor.ListEditor<>(view, items);
    }

}