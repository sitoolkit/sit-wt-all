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

import javax.annotation.Resource;

import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.domain.testscript.TestStep;

/**
 * 要素の属性値を変数に格納する操作です。
 *
 * @author hiroyuki.takeda
 */
@Component
public class StoreElementValueOperation extends SeleniumOperation {

    @Resource
    TestContext context;

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {

        Object value = null;
        String name = testStep.getValue();
        String[] type = testStep.getDataType().split(":");
        WebElement element = findElement(testStep.getLocator());

        switch (type[0]) {

            case "":
            case "text":
                value = element.getText();
                break;
            case "tag":
                value = element.getTagName();
                break;
            case "attribute":
                if (1 < type.length)
                    value = element.getAttribute(type[1]);
                break;
            case "css":
                if (1 < type.length)
                    value = element.getCssValue(type[1]);
                break;
            case "location":
                value = element.getLocation();
                break;
            case "rect":
                if (pm.isChromeDriver()) {
                    value = new Rectangle(element.getLocation(), element.getSize());
                } else {
                    value = element.getRect();
                }
                break;
            case "size":
                value = element.getSize();
                break;
        }

        if (value != null) {
            ctx.info(element, "var.define", name, value);
            context.addParam(name, value);
        }
    }
}
