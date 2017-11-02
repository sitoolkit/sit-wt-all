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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class StoreElementIndexOperation extends SeleniumOperation {

    @Resource
    TestContext context;

    private int indexDefault = 1;

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        WebElement element = findElement(testStep.getLocator());
        WebElement parent = element.findElement(By.xpath(".."));
        int idx = getIndexDefault();

        for (WebElement child : parent.findElements(By.xpath("/*"))) {
            if (element.getLocation().equals(child.getLocation())) {
                break;
            }
            idx++;
        }

        ctx.info(element, "element.index", testStep.getItemName(), testStep.getLocator(), idx);

        context.addParam(testStep.getDataType(), idx);
    }

    public int getIndexDefault() {
        return indexDefault;
    }

    public void setIndexDefault(int indexDefault) {
        this.indexDefault = indexDefault;
    }
}
