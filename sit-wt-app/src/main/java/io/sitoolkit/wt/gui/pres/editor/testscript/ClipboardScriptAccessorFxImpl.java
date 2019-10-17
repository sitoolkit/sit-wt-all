package io.sitoolkit.wt.gui.pres.editor.testscript;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import lombok.Value;

public class ClipboardScriptAccessorFxImpl implements ClipboardScriptAccessor {

  private static final String MINE_TYPE_CELL = "io.sitoolkit.wt.testscript.cell";
  private static final DataFormat DATAFORMAT_CELL;

  static {
    DataFormat fmt;
    if ((fmt = DataFormat.lookupMimeType(MINE_TYPE_CELL)) == null) {
      fmt = new DataFormat(MINE_TYPE_CELL);
    }
    DATAFORMAT_CELL = fmt;
  }

  TestScriptEditorFxImpl editor;

  public ClipboardScriptAccessorFxImpl(TestScriptEditorFxImpl editor) {
    this.editor = editor;
  }

  @Override
  public void pasteCase() {
    // TODO Auto-generated method stub

  }

  @Override
  public void pasteStep() {
    // TODO Auto-generated method stub

  }

  @Override
  public void pasteCaseTail() {
    // TODO Auto-generated method stub

  }

  @Override
  public void pasteStepTail() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean hasClipboardCases() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean hasClipboardSteps() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean clipboardPastable() {
    return Clipboard.getSystemClipboard().hasContent(DATAFORMAT_CELL)
        && editor.getSelection().getSelectedCells().size() == 1;
  }

  @Override
  public void copy() {
    TableViewSelectionModel<ScriptEditorRow> selection = editor.getSelection();

    @SuppressWarnings("rawtypes")
    List<TablePosition> posList = selection.getSelectedCells();

    if (posList.isEmpty()) {
      return;
    }
    int leftColumn = posList.stream().mapToInt(TablePosition::getColumn).min().getAsInt();
    int topRow = posList.stream().mapToInt(TablePosition::getRow).min().getAsInt();
    List<Cell> selectedCells =
        posList.stream().map(p -> createCellData(p, topRow, leftColumn)).collect(toList());

    final ClipboardContent content = new ClipboardContent();
    content.put(DATAFORMAT_CELL, selectedCells);
    content.putString(toTsvString(selectedCells));
    Clipboard.getSystemClipboard().setContent(content);
  }

  @Override
  public void paste() {

    if (!clipboardPastable()) {
      return;
    }
    TablePosition<?, ?> position = editor.getSelection().getSelectedCells().get(0);

    @SuppressWarnings("unchecked")
    List<Cell> cells = (List<Cell>) Clipboard.getSystemClipboard().getContent(DATAFORMAT_CELL);
    cells.stream().forEach(cell -> writeCellData(position, cell));
  }

  private Cell createCellData(TablePosition<?, ?> position, int topRow, int leftColumn) {
    return new Cell(
        position.getRow() - topRow,
        position.getColumn() - leftColumn,
        editor.getCellValue(position.getRow(), position.getColumn()));
  }

  private void writeCellData(TablePosition<?, ?> position, Cell cell) {
    editor.setCellValue(
        position.getRow() + cell.getRow(),
        position.getColumn() + cell.getColumn(),
        cell.getValue());
  }

  private String toTsvString(List<Cell> cells) {
    String[][] grid = createGrid(cells);
    return Stream.of(grid)
        .map(row -> Stream.of(row).map(StringUtils::defaultString).collect(joining("\t")))
        .collect(joining("\n"));
  }

  private String[][] createGrid(List<Cell> cells) {
    int row = cells.stream().mapToInt(Cell::getRow).max().getAsInt();
    int column = cells.stream().mapToInt(Cell::getColumn).max().getAsInt();
    String[][] grid = new String[row + 1][column + 1];
    cells.forEach(
        c -> {
          grid[c.getRow()][c.getColumn()] = c.getValue();
        });
    return grid;
  }

  @Value
  static class Cell implements Serializable {
    private static final long serialVersionUID = 1L;
    private int row;
    private int column;
    private String value;
  }
}
