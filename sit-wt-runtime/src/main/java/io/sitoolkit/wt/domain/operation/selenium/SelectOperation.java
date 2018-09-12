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
package io.sitoolkit.wt.domain.operation.selenium;

import java.util.Arrays;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

import io.sitoolkit.wt.domain.evidence.MessagePattern;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.resource.MessageManager;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class SelectOperation extends SeleniumOperation {

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        WebElement element = findElement(testStep.getLocator());
        ctx.info(element, MessagePattern.項目にXXをYYします, Arrays.toString(testStep.getValues()),
                MessageManager.getMessage("select"));
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

                // Edge webdriver bug: changing <select> does not fire onChange
                // event
                // https://connect.microsoft.com/IE/Feedback/Details/2204921
                if (pm.isEdgeDriver()) {
                    selectByVisibleTextForEdge(element, select, value);
                } else {
                    select.selectByVisibleText(value);
                }

            } else {
                sb.append("値=" + value);
                select.selectByValue(value);
            }
        }

    }

    protected void selectByVisibleTextForEdge(WebElement element, Select select, String value) {
        boolean selected = false;
        for (WebElement option : select.getOptions()) {
            if (value.equals(option.getText())) {
                ((JavascriptExecutor) seleniumDriver)
                        .executeScript("arguments[0].selected = 'selected';", option);
                selected = true;
                ((JavascriptExecutor) seleniumDriver).executeScript(
                        "if (typeof arguments[0].onchange == 'function') {arguments[0].onchange();}",
                        element);
                break;
            }
        }

        if (!selected) {
            throw new NoSuchElementException("Cannot locate element with text: " + value);
        }
    }
}
