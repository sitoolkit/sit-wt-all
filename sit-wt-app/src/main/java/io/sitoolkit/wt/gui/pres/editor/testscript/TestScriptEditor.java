package io.sitoolkit.wt.gui.pres.editor.testscript;

import java.util.List;
import org.controlsfx.control.spreadsheet.GridChange;
import io.sitoolkit.wt.domain.testscript.TestScript;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;

public interface TestScriptEditor {

  void load(TestScript testScript);

  ContextMenu getContextMenu();

  TestScript buildTestScript();

  Node getSpreadSheet();

  void setDebugStyle(int nextStepIndex, int caseIndex);

  void removeDebugStyle();

  boolean insertTestCase();

  void appendTestCase();

  boolean insertTestStep();

  void appendTestStep();

  int getCaseCount(List<GridChange> changeList);

  boolean insertTestCases(int count);

  void pasteClipboard();

  int getStepCount(List<GridChange> changeList);

  boolean insertTestSteps(int count);

  void appendTestCases(int count);

  void appendTestSteps(int count);

  void deleteTestCase();

  void deleteTestStep();

  boolean isCellSelected();

  boolean isCaseSelected();

  boolean isStepSelected();

  boolean isCaseInsertable();

  boolean isStepInsertable();

  void toggleBreakpoint();

  ClipboardScriptAccessor getClipboardAccessor();
}
