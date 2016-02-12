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

import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.RegexHelper;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class WaitOperation extends SeleniumOperation {

    private int timeout = 1;
    private int waitSpan = 100;

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        ctx.info("{}({})に{}が表示されるまで{}秒間待機します。", testStep.getItemName(), testStep.getLocator(),
                testStep.getValue(), getTimeout());
        int count = (getTimeout() * 1000) / getWaitSpan();

        for (int i = 0; i < count; i++) {

            String text = findElement(testStep.getLocator()).getText();
            if (RegexHelper.matches(testStep.getValue(), text)) {
                break;
            }
            try {
                Thread.sleep(getWaitSpan());
            } catch (InterruptedException e) {
                log.warn("スレッドの待機に失敗しました。", e);
            }
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getWaitSpan() {
        return waitSpan;
    }

    public void setWaitSpan(int waitSpan) {
        this.waitSpan = waitSpan;
    }
}
