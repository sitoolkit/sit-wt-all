/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.domain.evidence.selenium;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.evidence.DialogScreenshotSupport;
import org.sitoolkit.wt.domain.operation.selenium.DialogOperation;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class SeleniumDialogScreenshotSupport implements DialogScreenshotSupport {

    protected final Logger log = LoggerFactory.getLogger(getClass());

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
            if (!testStep.isSkip()
                    && testStep.getOperation() instanceof DialogOperation
                    && testStep.beforeScreenshot()) {
                int reservedIdx = i;
                while(reservedIdx > 0) {
                    TestStep reservedScript = testSteps.get(reservedIdx);
                    reservedScript.setCurrentCaseNo(caseNo);
                    if (reservedScript.isSkip()) {
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
        log.debug("ウィンドウサイズの事前取得が必要なステップNo:{}", windowSizeCheckNoSet);
    }

    @Override
    public void reserveWindowRect(String testStepNo) {
        if (windowSizeCheckNoSet.contains(testStepNo)) {
            log.debug("ウィンドウ位置、サイズを取得します");
            Point winPos = seleniumDriver.manage().window().getPosition();
            Dimension winSize = seleniumDriver.manage().window().getSize();
            current.setWindowRect(winPos.getX(), winPos.getY(), winSize.getWidth(), winSize.getHeight());
        }
    }
}
