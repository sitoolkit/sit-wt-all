/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.sitoolkit.wt.domain.evidence.selenium;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import io.sitoolkit.wt.domain.evidence.DialogScreenshotSupport;
import io.sitoolkit.wt.domain.operation.selenium.DialogOperation;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

/**
 *
 * @author yuichi.kuwahara
 */
public class SeleniumDialogScreenshotSupport implements DialogScreenshotSupport {

  protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

  private Set<String> windowSizeCheckNoSet = new HashSet<String>();

  @Resource
  WebDriver seleniumDriver;

  @Resource
  TestContext current;

  @Override
  public void checkReserve(List<TestStep> testSteps, String caseNo) {

    for (int i = testSteps.size() - 1; i >= 0; i--) {
      TestStep testStep = testSteps.get(i);
      testStep.setCurrentCaseNo(caseNo);
      if (testStep.isCaseStrExists() && testStep.getOperation() instanceof DialogOperation
          && testStep.beforeScreenshot()) {
        int reservedIdx = i;
        while (reservedIdx > 0) {
          TestStep reservedScript = testSteps.get(reservedIdx);
          reservedScript.setCurrentCaseNo(caseNo);
          if (!reservedScript.isCaseStrExists()) {
            continue;
          }
          reservedIdx--;
          if (reservedIdx == i - 2) {
            windowSizeCheckNoSet.add(reservedScript.getNo());
            testStep.addDialogScreenshotCaseNo(caseNo);
            break;
          }
        }
      }
    }
    log.debug("window.size.check", windowSizeCheckNoSet);
  }

  @Override
  public void reserveWindowRect(String testStepNo) {
    if (windowSizeCheckNoSet.contains(testStepNo)) {
      log.debug("window.get");
      Point winPos = seleniumDriver.manage().window().getPosition();
      Dimension winSize = seleniumDriver.manage().window().getSize();
      current.setWindowRect(winPos.getX(), winPos.getY(), winSize.getWidth(), winSize.getHeight());
    }
  }
}
