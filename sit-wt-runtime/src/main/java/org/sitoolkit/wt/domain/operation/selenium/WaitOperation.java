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
package org.sitoolkit.wt.domain.operation.selenium;

import javax.annotation.Resource;

import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.RegexHelper;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class WaitOperation extends SeleniumOperation {

    @Resource
    PropertyManager pm;

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        int timeout = pm.getTimeout();
        int waitSpan = pm.getWaitSpan();

        ctx.info("{}({})に{}が表示されるまで{}秒間待機します。", testStep.getItemName(), testStep.getLocator(),
                testStep.getValue(), timeout / 1000);
        int count = timeout / waitSpan;

        for (int i = 0; i < count; i++) {

            String text = findElement(testStep.getLocator()).getText();
            if (RegexHelper.matches(testStep.getValue(), text)) {
                break;
            }
            try {
                Thread.sleep(waitSpan);
            } catch (InterruptedException e) {
                sitLog.warn("thread.sleep.error", e);
            }
        }
    }
}
