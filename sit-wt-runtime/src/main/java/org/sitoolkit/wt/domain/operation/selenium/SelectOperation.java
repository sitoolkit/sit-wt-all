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
import org.openqa.selenium.support.ui.Select;
import org.sitoolkit.wt.domain.evidence.MessagePattern;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class SelectOperation extends SeleniumOperation {

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        WebElement element = findElement(testStep.getLocator());
        Select select = new Select(element);

        StringBuilder sb = new StringBuilder();

        for (String value : testStep.getValues()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }

            if ("index".equals(testStep.getDataType())) {
                sb.append(value + "番目");
                select.selectByIndex(Integer.parseInt(value) - 1);
            } else if ("label".equals(testStep.getDataType())) {
                sb.append(value);
                select.selectByVisibleText(value);
            } else {
                sb.append("値=" + value);
                select.selectByValue(value);
            }
        }

        ctx.info(element, MessagePattern.項目にXXをYYします, sb.toString() + "の選択肢", "選択");
    }
}
