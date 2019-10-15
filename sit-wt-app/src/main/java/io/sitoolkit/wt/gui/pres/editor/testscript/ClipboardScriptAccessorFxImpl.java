package io.sitoolkit.wt.gui.pres.editor.testscript;

import static java.util.stream.Collectors.joining;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codehaus.plexus.util.StringUtils;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import lombok.Getter;
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
  public boolean hasClipboardCells() {
    return Clipboard.getSystemClipboard().hasContent(DATAFORMAT_CELL);
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
        posList
            .stream()
            .map(p -> createCellData(editor, p, topRow, leftColumn))
            .collect(Collectors.toCollection(ArrayList::new));

    Grid grid = new Grid(selectedCells);

    final ClipboardContent content = new ClipboardContent();
    content.put(DATAFORMAT_CELL, grid);
    content.putString(grid.toTsvString());
    Clipboard.getSystemClipboard().setContent(content);
  }

  private Cell createCellData(
      TestScriptEditorFxImpl editor, TablePosition<?, ?> position, int topRow, int leftColumn) {
    return new Cell(
        position.getRow() - topRow,
        position.getColumn() - leftColumn,
        editor.getCellValue(position));
  }

  static class Grid implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter private List<Cell> cells;

    private transient String[][] grid;

    public Grid(List<Cell> cells) {
      this.cells = cells;
    }

    public String toTsvString() {
      return Stream.of(getGrid())
          .map(row -> Stream.of(row).map(StringUtils::defaultString).collect(joining("\t")))
          .collect(joining("\n"));
    }

    private String[][] getGrid() {
      if (grid == null) {
        int row = cells.stream().mapToInt(Cell::getRow).max().getAsInt();
        int column = cells.stream().mapToInt(Cell::getColumn).max().getAsInt();
        grid = new String[row + 1][column + 1];
        cells.forEach(
            c -> {
              grid[c.getRow()][c.getColumn()] = c.getContent();
            });
      }
      return grid;
    }
  }

  @Value
  static class Cell implements Serializable {
    private static final long serialVersionUID = 1L;
    private int row;
    private int column;
    private String content;
  }
}
