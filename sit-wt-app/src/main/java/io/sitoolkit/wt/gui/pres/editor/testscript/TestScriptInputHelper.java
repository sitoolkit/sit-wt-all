package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.List;
import java.util.stream.IntStream;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import io.sitoolkit.wt.domain.testscript.ScreenshotTiming;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.domain.testscript.TestStepInputType;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TestScriptInputHelper {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(TestScriptInputHelper.class);

  private static final int ROW_SPAN = 1;
  private static final int COL_SPAN = 1;

  private static final int COLUMN_INDEX_FIRST_CASE = 7;

  private static final int COL_INDEX_OPERATION = 2;
  private static final int COL_INDEX_LOCATOR_TYPE = 3;
  private static final int COL_INDEX_LOCATOR = 4;
  private static final int COL_INDEX_DATA_TYPE = 5;
  private static final int COL_INDEX_SCREENSHOT = 6;

  private static final ReadOnlyCellType READ_ONLY_TYPE = new ReadOnlyCellType();
  private static final DisabledCellType DISABLED_TYPE = new DisabledCellType();
  private static final OkCancelCellType OK_CANCEL_TYPE = new OkCancelCellType();
  private static final SpreadsheetCellType.ListType SCREENSHOT_TYPE =
      SpreadsheetCellType.LIST(ScreenshotTiming.getLabels());

  private OperationCellType OPERATION_TYPE = new OperationCellType(this::updateStepOperation);

  private SpreadsheetView spreadSheet;

  public TestScriptInputHelper(SpreadsheetView spreadSheet) {
    this.spreadSheet = spreadSheet;
  }

  public ObservableList<SpreadsheetCell> buildTestStepRow(int rowIndex, TestStep testStep) {
    ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();

    cells.add(buildStringCell(rowIndex, cells.size(), testStep.getNo()));
    cells.add(buildStringCell(rowIndex, cells.size(), testStep.getItemName()));
    String operationName = testStep.getOperationName();
    cells.add(buildOperationCell(rowIndex, operationName));
    cells.add(buildLocatorTypeCell(operationName, rowIndex, testStep.getLocator().getType()));
    cells.add(buildLocatorCell(operationName, rowIndex, testStep.getLocator().getValue()));
    cells.add(buildDataTypeCell(operationName, rowIndex, testStep.getDataType()));
    cells.add(buildScreenshotCell(rowIndex, testStep.getScreenshotTiming().getLabel()));

    testStep.getTestData().values().stream().forEach(testData -> {
      cells.add(buildDataCell(operationName, rowIndex, cells.size(), testData));
    });

    return cells;
  }

  public SpreadsheetCell buildStringCell(int rowIndex, int colIndex, String value) {
    return SpreadsheetCellType.STRING.createCell(rowIndex, colIndex, ROW_SPAN, COL_SPAN, value);
  }

  public SpreadsheetCell buildReadOnlyCell(int rowIndex, int colIndex, String value) {
    return READ_ONLY_TYPE.createCell(rowIndex, colIndex, ROW_SPAN, COL_SPAN, value);
  }

  private SpreadsheetCell buildDisabledCell(int rowIndex, int colIndex) {
    return DISABLED_TYPE.createCell(rowIndex, colIndex, ROW_SPAN, COL_SPAN);
  }

  private SpreadsheetCell buildOperationCell(int rowIndex, String value) {
    return OPERATION_TYPE.createCell(rowIndex, COL_INDEX_OPERATION, ROW_SPAN, COL_SPAN, value);
  }

  private SpreadsheetCell buildLocatorTypeCell(String operationName, int rowIndex, String value) {
    List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
    if (locatorTypes.size() <= 1) {
      return buildDisabledCell(rowIndex, COL_INDEX_LOCATOR_TYPE);
    } else {
      return SpreadsheetCellType.LIST(locatorTypes).createCell(rowIndex, COL_INDEX_LOCATOR_TYPE,
          ROW_SPAN, COL_SPAN, value);
    }
  }

  private SpreadsheetCell buildLocatorCell(String operationName, int rowIndex, String value) {
    List<String> locatorTypes = TestStepInputType.decode(operationName).getLocatorTypes();
    if (locatorTypes.size() == 1 && locatorTypes.get(0).equals("")) {
      return buildDisabledCell(rowIndex, COL_INDEX_LOCATOR);
    } else {
      return buildStringCell(rowIndex, COL_INDEX_LOCATOR, value);
    }
  }

  private SpreadsheetCell buildDataTypeCell(String operationName, int rowIndex, String value) {
    List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
    if (dataTypes.size() <= 1) {
      return buildDisabledCell(rowIndex, COL_INDEX_DATA_TYPE);
    } else {
      return SpreadsheetCellType.LIST(dataTypes).createCell(rowIndex, COL_INDEX_DATA_TYPE, ROW_SPAN,
          COL_SPAN, value);
    }
  }

  private SpreadsheetCell buildScreenshotCell(int rowIndex, String value) {
    return SCREENSHOT_TYPE.createCell(rowIndex, COL_INDEX_SCREENSHOT, ROW_SPAN, COL_SPAN, value);
  }

  private SpreadsheetCell buildDataCell(String operationName, int rowIndex, int colIndex,
      String value) {

    List<String> dataTypes = TestStepInputType.decode(operationName).getDataTypes();
    switch (dataTypes.get(0)) {
      case "ok_cancel":
        return OK_CANCEL_TYPE.createCell(rowIndex, colIndex, ROW_SPAN, COL_SPAN, value);
      case "na":
        return buildDisabledCell(rowIndex, colIndex);
      default:
        return buildStringCell(rowIndex, colIndex, value);
    }
  }

  public void updateStepOperation(int rowIndex, String operationName) {
    ObservableList<SpreadsheetCell> cells = spreadSheet.getGrid().getRows().get(rowIndex);

    cells.set(COL_INDEX_LOCATOR_TYPE, buildLocatorTypeCell(operationName, rowIndex, null));
    cells.set(COL_INDEX_LOCATOR, buildLocatorCell(operationName, rowIndex, null));
    cells.set(COL_INDEX_DATA_TYPE, buildDataTypeCell(operationName, rowIndex, null));

    IntStream.range(COLUMN_INDEX_FIRST_CASE, cells.size()).forEach((colIndex) -> {
      cells.set(colIndex, buildDataCell(operationName, rowIndex, colIndex, null));
    });

    SpreadsheetUtils.forceRedraw(spreadSheet);
  }

  public ObservableList<SpreadsheetCell> buildEmptyRow(int rowIndex, int columnCount) {
    ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();
    String operationName = "";

    cells.add(buildStringCell(rowIndex, cells.size(), null));
    cells.add(buildStringCell(rowIndex, cells.size(), null));
    cells.add(buildOperationCell(rowIndex, operationName));
    cells.add(buildLocatorTypeCell(operationName, rowIndex, null));
    cells.add(buildLocatorCell(operationName, rowIndex, null));
    cells.add(buildDataTypeCell(operationName, rowIndex, null));
    cells.add(buildScreenshotCell(rowIndex, null));

    IntStream.range(COLUMN_INDEX_FIRST_CASE, columnCount).forEach(colIndex -> {
      cells.add(buildDataCell(operationName, rowIndex, cells.size(), null));
    });

    return cells;
  }

  public SpreadsheetCell buildEmptyDataCell(ObservableList<SpreadsheetCell> row, int rowIndex,
      int colIndex) {

    String operationName = row.get(COL_INDEX_OPERATION).getText();
    return buildDataCell(operationName, rowIndex, colIndex, null);
  }

  public SpreadsheetCell rebuildCell(SpreadsheetCell original, int rowIndex, int colIndex) {
    SpreadsheetCellType<?> type = original.getCellType();
    String value = original.getText();

    if (type instanceof SpreadsheetCellType.StringType) {
      return buildStringCell(rowIndex, colIndex, value);
    } else if (type instanceof SpreadsheetCellType.ListType) {
      return ((SpreadsheetCellType.ListType) type).createCell(rowIndex, colIndex, ROW_SPAN,
          COL_SPAN, value);
    } else if (type instanceof NormalStringCellType) {
      return ((NormalStringCellType) type).createCell(rowIndex, colIndex, ROW_SPAN, COL_SPAN,
          value);
    } else {
      LOG.warnMsg("Illegal operation for " + type);
      return null;
    }
  }
}
