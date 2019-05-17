package io.sitoolkit.wt.gui.pres;

import java.io.File;
import java.util.List;

public interface TestRunnable {

  void runTest(boolean isDebug, boolean isParallel, File testScript, List<String> caseNos);

  void run();
}
