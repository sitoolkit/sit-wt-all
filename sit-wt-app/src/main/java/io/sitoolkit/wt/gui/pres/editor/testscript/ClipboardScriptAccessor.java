package io.sitoolkit.wt.gui.pres.editor.testscript;

public interface ClipboardScriptAccessor {

  void copy();

  void paste();

  int getClipboardCaseCount();

  int getClipboardStepCount();

  boolean hasClipboardCases();

  boolean hasClipboardSteps();

  boolean clipboardPastable();
}
