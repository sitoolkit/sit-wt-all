package io.sitoolkit.wt.gui.pres.editor.testscript;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class TestScriptEditorCellBuilder {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(TestScriptEditorCellBuilder.class);

    public SpreadsheetCell build(SpreadsheetCellType<?> type, int rowIndex, int colIndex,
            String value) {
        if (type instanceof SpreadsheetCellType.StringType) {
            return ((SpreadsheetCellType.StringType) type).createCell(rowIndex, colIndex, 1, 1,
                    value);
        } else if (type instanceof SpreadsheetCellType.ListType) {
            return ((SpreadsheetCellType.ListType) type).createCell(rowIndex, colIndex, 1, 1,
                    value);
        } else {
            LOG.warn("Illegal operation for {}", type);
            return null;
        }
    }

}
