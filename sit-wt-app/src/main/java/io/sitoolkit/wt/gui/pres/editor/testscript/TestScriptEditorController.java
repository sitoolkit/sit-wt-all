package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.nio.file.Path;
import java.util.Optional;
import io.sitoolkit.wt.domain.debug.DebugListener;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.gui.app.script.ScriptService;
import io.sitoolkit.wt.gui.pres.editor.EditorController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import lombok.Getter;
import lombok.NonNull;

public class TestScriptEditorController implements EditorController, DebugListener {

  private TestScriptEditor editor = new TestScriptEditorFxImpl();

  private MenuState menuState = new MenuState();

  @NonNull private ScriptService scriptService;

  public TestScriptEditorController(ScriptService scriptService) {
    this.scriptService = scriptService;
  }

  @Override
  public void open(Path file) {
    TestScript testScript = scriptService.read(file.toFile());
    editor.load(testScript);
    editor.getContextMenu().getItems().addAll(createMenuItems());
    editor.getContextMenu().setOnShowing(e -> updateManuState());
  }

  @Override
  public void save() {
    TestScript testScript = editor.buildTestScript();
    scriptService.write(testScript);
  }

  @Override
  public Optional<Node> getEditorContent() {
    return Optional.of(editor.getSpreadSheet());
  }

  @Override
  public void saveAs(Path file) {
    TestScript testScript = editor.buildTestScript();
    testScript.setScriptFile(file.toFile());
    scriptService.write(testScript);
  }

  @Override
  public void onDebugging(Path scriptPath, int nextStepIndex, int caseIndex) {
    editor.setDebugStyle(nextStepIndex, caseIndex);
  }

  @Override
  public void onCaseEnd(Path scriptPath, int caseIndex) {
    editor.removeDebugStyle();
  }

  @Override
  public void onClose() {
    editor.removeDebugStyle();
  }

  public void newTestCase(ActionEvent e) {
    editor.insertTestCase();
  }

  public void newTestStep(ActionEvent e) {
    editor.insertTestStep();
  }

  public void newTestCaseTail(ActionEvent e) {
    editor.appendTestCase();
  }

  public void newTestStepTail(ActionEvent e) {
    editor.appendTestStep();
  }

  public void pasteCase(ActionEvent e) {
    editor.getClipboardAccessor().pasteCase();
  }

  public void pasteStep(ActionEvent e) {
    editor.getClipboardAccessor().pasteStep();
  }

  public void pasteCaseTail(ActionEvent e) {
    editor.getClipboardAccessor().pasteCaseTail();
  }

  public void pasteStepTail(ActionEvent e) {
    editor.getClipboardAccessor().pasteStepTail();
  }

  public void deleteCase(ActionEvent e) {
    editor.deleteTestCase();
  }

  public void deleteStep(ActionEvent e) {
    editor.deleteTestStep();
  }

  private void updateManuState() {
    menuState.getClipboardHasCase().set(hasClipboardCases());
    menuState.getClipboardHasStep().set(hasClipboardSteps());
    menuState.getCaseSelected().set(editor.isCaseSelected());
    menuState.getStepSelected().set(editor.isStepSelected());
    menuState.getCaseInsertable().set(editor.isCaseInsertable());
    menuState.getStepInsertable().set(editor.isStepInsertable());
  }

  public void toggleBreakpoint(ActionEvent e) {
    editor.toggleBreakpoint();
  }

  private ObservableList<MenuItem> createMenuItems() {
    ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    MenuItem item;
    Menu menu;

    menuItems.add(new SeparatorMenuItem());

    menu = new Menu("挿入");
    item = new MenuItem("新規ケースの挿入");
    item.setMnemonicParsing(false);
    item.setOnAction(this::newTestCase);
    item.disableProperty().bind(menuState.getCaseInsertable().not());
    menu.getItems().add(item);

    item = new MenuItem("コピーしたケースの挿入");
    item.setMnemonicParsing(false);
    item.setOnAction(this::pasteCase);
    item.disableProperty()
        .bind(menuState.getCaseInsertable().and(menuState.getClipboardHasCase()).not());
    menu.getItems().add(item);

    item = new MenuItem("新規ステップの挿入");
    item.setMnemonicParsing(false);
    item.setOnAction(this::newTestStep);
    item.disableProperty().bind(menuState.getStepInsertable().not());
    menu.getItems().add(item);

    item = new MenuItem("コピーしたステップの挿入");
    item.setMnemonicParsing(false);
    item.setOnAction(this::pasteStep);
    item.disableProperty()
        .bind(menuState.getStepInsertable().and(menuState.getClipboardHasStep()).not());
    menu.getItems().add(item);

    menuItems.add(menu);

    menu = new Menu("追加");

    item = new MenuItem("新規ステップを末尾に追加");
    item.setMnemonicParsing(false);
    item.setOnAction(this::newTestStepTail);
    menu.getItems().add(item);

    item = new MenuItem("新規ケースを末尾に追加");
    item.setMnemonicParsing(false);
    item.setOnAction(this::newTestCaseTail);
    menu.getItems().add(item);

    item = new MenuItem("コピーしたケースを末尾に追加");
    item.setMnemonicParsing(false);
    item.setOnAction(this::pasteCaseTail);
    item.disableProperty().bind(menuState.getClipboardHasCase().not());
    menu.getItems().add(item);

    item = new MenuItem("コピーしたステップを末尾に追加");
    item.setMnemonicParsing(false);
    item.setOnAction(this::pasteStepTail);
    item.disableProperty().bind(menuState.getClipboardHasStep().not());
    menu.getItems().add(item);

    menuItems.add(menu);

    menu = new Menu("削除");

    item = new MenuItem("ステップを削除");
    item.setMnemonicParsing(false);
    item.setOnAction(this::deleteStep);
    item.disableProperty().bind(menuState.getStepSelected().not());
    menu.getItems().add(item);

    item = new MenuItem("ケースを削除");
    item.setMnemonicParsing(false);
    item.setOnAction(this::deleteCase);
    item.disableProperty().bind(menuState.getCaseSelected().not());
    menu.getItems().add(item);

    menuItems.add(menu);

    menuItems.add(new SeparatorMenuItem());

    item = new MenuItem("ブレークポイント有効化/無効化");
    item.setOnAction(this::toggleBreakpoint);
    menuItems.add(item);

    return menuItems;
  }

  private boolean hasClipboardSteps() {
    return editor.getClipboardAccessor().hasClipboardSteps();
  }

  private boolean hasClipboardCases() {
    return editor.getClipboardAccessor().hasClipboardCases();
  }

  @Getter
  private class MenuState {
    private BooleanProperty clipboardHasCase = new SimpleBooleanProperty(false);
    private BooleanProperty clipboardHasStep = new SimpleBooleanProperty(false);
    private BooleanProperty caseSelected = new SimpleBooleanProperty(false);
    private BooleanProperty stepSelected = new SimpleBooleanProperty(false);
    private BooleanProperty caseInsertable = new SimpleBooleanProperty(false);
    private BooleanProperty stepInsertable = new SimpleBooleanProperty(false);
  }
}
