package org.sitoolkit.wt.gui.pres.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.stage.WindowEvent;

public class TestScriptEditorController {

    static final DataFormat DATAFORMAT_SPREADSHEET;
    static {
        DataFormat fmt;
        if ((fmt = DataFormat.lookupMimeType("SpreadsheetView")) == null) {
            fmt = new DataFormat("SpreadsheetView");
        }
        DATAFORMAT_SPREADSHEET = fmt;
    }
    static TestScriptEditor editor = new TestScriptEditor();
    final SpreadsheetView spreadSheet;
    final List<EditorMenuItem> myItems = new ArrayList<EditorMenuItem>();

    public TestScriptEditorController(SpreadsheetView spreadSheet) {
        this.spreadSheet = spreadSheet;

        spreadSheet.getContextMenu().getItems().addAll(createMenuItems());

        EventHandler<WindowEvent> defaultHandler = spreadSheet.getContextMenu().getOnShowing();
        spreadSheet.getContextMenu().setOnShowing(e -> {
            onContextMenuShowing(e);
            defaultHandler.handle(e);
        });
    }

    public void newTestCase(ActionEvent e) {
        editor.insertTestCase(spreadSheet);
    }

    public void newTestStep(ActionEvent e) {
        editor.insertTestStep(spreadSheet);
    }

    public void newTestCaseTail(ActionEvent e) {
        editor.appendTestCase(spreadSheet);
    }

    public void newTestStepTail(ActionEvent e) {
        editor.appendTestStep(spreadSheet);
    }

    public void pasteCase(ActionEvent e) {

        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

            @SuppressWarnings("unchecked")
            List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
            int count = editor.getCaseCount(spreadSheet, changeList);
            if (count > 0) {
                if (editor.insertTestCases(spreadSheet, count)) {
                    spreadSheet.pasteClipboard();
                }
            }
        }
    }

    public void pasteStep(ActionEvent e) {
        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

            @SuppressWarnings("unchecked")
            List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
            int count = editor.getStepCount(spreadSheet, changeList);
            if (count > 0) {
                if (editor.insertTestSteps(spreadSheet, count)) {
                    spreadSheet.pasteClipboard();
                }
            }
        }
    }

    public void pasteCaseTail(ActionEvent e) {
        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

            @SuppressWarnings("unchecked")
            List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
            int count = editor.getCaseCount(spreadSheet, changeList);
            if (count > 0) {
                editor.appendTestCases(spreadSheet, count);
                spreadSheet.pasteClipboard();
            }
        }
    }

    public void pasteStepTail(ActionEvent e) {
        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {

            @SuppressWarnings("unchecked")
            List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
            int count = editor.getStepCount(spreadSheet, changeList);
            if (count > 0) {
                editor.appendTestSteps(spreadSheet, count);
                spreadSheet.pasteClipboard();
            }
        }
    }


    public void deleteCase(ActionEvent e) {
        editor.deleteTestCase(spreadSheet);
    }

    public void deleteStep(ActionEvent e) {
        editor.deleteTestStep(spreadSheet);
    }


    public void onContextMenuShowing(WindowEvent e) {
        myItems.stream().forEach(item -> item.refreshDisable(spreadSheet));
    }

    private ObservableList<MenuItem> createMenuItems() {
        ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
        EditorMenuItem item;
        Menu menu;

        menuItems.add(new SeparatorMenuItem());

        menu = new Menu("挿入");
        item = new EditorMenuItem("新規ケースの挿入");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> newTestCase(e));
        item.setExcutableTest(spreadSheet -> editor.isCaseInsertable(spreadSheet));
        menu.getItems().add(item);
        myItems.add(item);

        item = new EditorMenuItem("コピーしたケースの挿入");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> pasteCase(e));
        item.setExcutableTest(spreadSheet -> editor.isCaseInsertable(spreadSheet) && hasClipboardCases());
        menu.getItems().add(item);
        myItems.add(item);

        item = new EditorMenuItem("新規ステップの挿入");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> newTestStep(e));
        item.setExcutableTest(spreadSheet -> editor.isStepInsertable(spreadSheet));
        menu.getItems().add(item);
        myItems.add(item);

        item = new EditorMenuItem("コピーしたステップの挿入");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> pasteStep(e));
        item.setExcutableTest(spreadSheet -> editor.isStepInsertable(spreadSheet) && hasClipboardSteps());
        menu.getItems().add(item);
        myItems.add(item);

        menuItems.add(menu);

        menu = new Menu("追加");

        item = new EditorMenuItem("新規ステップを末尾に追加");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> newTestStepTail(e));
        menu.getItems().add(item);
        myItems.add(item);

        item = new EditorMenuItem("新規ケースを末尾に追加");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> newTestCaseTail(e));
        menu.getItems().add(item);
        myItems.add(item);

        item = new EditorMenuItem("コピーしたケースを末尾に追加");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> pasteCaseTail(e));
        item.setExcutableTest(spreadSheet -> hasClipboardCases());
        menu.getItems().add(item);
        myItems.add(item);

        item = new EditorMenuItem("コピーしたステップを末尾に追加");
        item.setMnemonicParsing(false);
        item.setOnAction(e -> pasteStepTail(e));
        item.setExcutableTest(spreadSheet -> hasClipboardSteps());
        menu.getItems().add(item);
        myItems.add(item);

        menuItems.add(menu);


        menu = new Menu("削除");

        item = new EditorMenuItem("ステップを削除");
        item.setMnemonicParsing(false);
        item.setOnAction(this::deleteStep);
        item.setExcutableTest(editor::isStepSelected);
        menu.getItems().add(item);
        myItems.add(item);

        item = new EditorMenuItem("ケースを削除");
        item.setMnemonicParsing(false);
        item.setOnAction(this::deleteCase);
        item.setExcutableTest(editor::isCaseSelected);
        menu.getItems().add(item);
        myItems.add(item);

        menuItems.add(menu);


        return menuItems;
    }

    private boolean hasClipboardSteps() {
        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {
            @SuppressWarnings("unchecked")
            List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
            return editor.getStepCount(spreadSheet, changeList) > 0;

        } else {
            return false;
        }
    }

    private boolean hasClipboardCases() {
        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasContent(DATAFORMAT_SPREADSHEET)) {
            @SuppressWarnings("unchecked")
            List<GridChange> changeList = (List<GridChange>) cb.getContent(DATAFORMAT_SPREADSHEET);
            return editor.getCaseCount(spreadSheet, changeList) > 0;

        } else {
            return false;
        }
    }

}

class EditorMenuItem extends MenuItem {

    public EditorMenuItem() {
        super();
    }

    public EditorMenuItem(String text, Node graphic) {
        super(text, graphic);
    }

    public EditorMenuItem(String text) {
        super(text);
    }

    Optional<Predicate<SpreadsheetView>> excutableTest = Optional.empty();

    public void setExcutableTest(Predicate<SpreadsheetView> excutableTest) {
        this.excutableTest = Optional.ofNullable(excutableTest);
    }

    public void refreshDisable(SpreadsheetView spreadSheet) {
        excutableTest.ifPresent(tester -> setDisable(!tester.test(spreadSheet)));
    }

}
