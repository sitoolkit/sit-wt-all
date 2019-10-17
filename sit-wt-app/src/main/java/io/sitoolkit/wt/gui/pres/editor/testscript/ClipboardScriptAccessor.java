package io.sitoolkit.wt.gui.pres.editor.testscript;

public interface ClipboardScriptAccessor {

  void pasteCase();

  void pasteStep();

  void pasteCaseTail();

  void pasteStepTail();

  boolean hasClipboardCases();

  boolean hasClipboardSteps();

  boolean clipboardPastable();

  void copy();

  void paste();
}
