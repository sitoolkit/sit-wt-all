package io.sitoolkit.wt.gui.app.test;

import java.util.List;
import io.sitoolkit.wt.domain.tester.TestResult;

@FunctionalInterface
public interface TestExitCallback {

  void callback(List<TestResult> restResults);
}
