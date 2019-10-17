package io.sitoolkit.wt.gui.pres.editor.testscript;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
  public int getClipboardCaseCount() {
    return countCases(readFromClipboard());
  }

  @Override
  public int getClipboardStepCount() {
    return countSteps(readFromClipboard());
  }

  @Override
  public boolean hasClipboardCases() {
    return getClipboardCaseCount() > 0;
  }

  @Override
  public boolean hasClipboardSteps() {
    return getClipboardStepCount() > 0;
  }

  private int countCases(List<Cell> changeList) {
    int columnCount = countColumns(changeList);
    int rowCount = countRows(changeList);
    return editor.getRowCount() == rowCount ? columnCount : 0;
  }

  private int countSteps(List<Cell> changeList) {
    int columnCount = countColumns(changeList);
    int rowCount = countRows(changeList);
    return editor.getColumnCount() == columnCount ? rowCount : 0;
  }

  private int countColumns(List<Cell> changeList) {
    Set<Integer> colSet =
        changeList.stream().map(change -> change.getColumn()).collect(Collectors.toSet());
    return countMinToMax(colSet);
  }

  private int countRows(List<Cell> changeList) {
    Set<Integer> rowSet =
        changeList.stream().map(change -> change.getRow()).collect(Collectors.toSet());
    return countMinToMax(rowSet);
  }

  private int countMinToMax(Collection<Integer> values) {
    return values.isEmpty()
        ? 0
        : values.stream().max(Comparator.naturalOrder()).get()
            - values.stream().min(Comparator.naturalOrder()).get()
            + 1;
  }

  @Override
  public boolean clipboardPastable() {
    return readFromClipboard().size() > 0 && editor.getSelection().getSelectedCells().size() == 1;
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
        posList.stream().map(p -> createDataFromEditor(p, topRow, leftColumn)).collect(toList());

    writeToClipboard(selectedCells);
  }

  @Override
  public void paste() {

    if (!clipboardPastable()) {
      return;
    }
    TablePosition<?, ?> position = editor.getSelection().getSelectedCells().get(0);
    readFromClipboard().stream().forEach(cell -> writeToEditor(position, cell));
  }

  private Cell createDataFromEditor(TablePosition<?, ?> position, int topRow, int leftColumn) {
    return new Cell(
        position.getRow() - topRow,
        position.getColumn() - leftColumn,
        editor.getCellValue(position.getRow(), position.getColumn()));
  }

  private void writeToEditor(TablePosition<?, ?> position, Cell cell) {
    editor.setCellValue(
        position.getRow() + cell.getRow(),
        position.getColumn() + cell.getColumn(),
        cell.getValue());
  }

  private List<Cell> readFromClipboard() {
    @SuppressWarnings("unchecked")
    List<Cell> cells = (List<Cell>) Clipboard.getSystemClipboard().getContent(DATAFORMAT_CELL);
    return cells != null ? cells : Collections.emptyList();
  }

  private void writeToClipboard(List<Cell> cells) {
    final ClipboardContent content = new ClipboardContent();
    content.put(DATAFORMAT_CELL, cells);
    content.putString(toTsvString(cells));
    Clipboard.getSystemClipboard().setContent(content);
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
