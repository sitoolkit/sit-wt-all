package io.sitoolkit.wt.gui.pres.editor.testscript;

public interface ScriptClipboardAccessor {

  void pasteCase();

  void pasteStep();

  void pasteCaseTail();

  void pasteStepTail();

  boolean hasClipboardCases();

  boolean hasClipboardSteps();
}
