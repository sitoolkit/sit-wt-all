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

import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.RegexHelper;
import org.sitoolkit.wt.infra.VerifyException;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class VerifyOperation extends SeleniumOperation {

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        String expected = testStep.getValue();
        WebElement element = findElement(testStep.getLocator());
        String actual = getActual(element, testStep);

        ctx.info(element, "{}({})の値が期待値[{}]に一致することを確認します。", testStep.getItemName(),
                testStep.getLocator(), expected);
        if (!RegexHelper.matches(testStep.getValue(), actual)) {
            throw new VerifyException("{0}({1})の値[{2}]は期待値[{3}]と異なります。", testStep.getItemName(),
                    testStep.getLocator(), actual, expected);
        }
    }

    protected String getActual(WebElement element, TestStep testStep) {
        return element.getText();
    }
}
