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

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.TestException;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class InputOperation extends SeleniumOperation {

    private static final String APPEND_PREFIX = "append:";

    @Override
    public void execute(TestStep testStep) {
        String value = testStep.getValue();
        WebElement element = findElement(testStep.getLocator());
        if (hasAttribute(element, "type", "file")) {
            File file = new File(value);
            if (file.exists()) {
                info(file.getAbsolutePath(), "入力", element);
                element.sendKeys(file.getAbsolutePath());
            } else {
                throw new TestException("指定されたファイルが存在しません " + file.getAbsolutePath());
            }
        } else {
            if (value.startsWith(APPEND_PREFIX)) {
                info(value, "入力(追記)", element);
                value = StringUtils.substringAfter(value, APPEND_PREFIX);
            } else {
                info(value, "入力(上書)", element);
                element.clear();
            }
            element.sendKeys(value);
        }
    }

    /**
     * WebElementが属性を持っている場合にtrueを返します。
     * このメソッドは、Appium系のWebElementでgetAttributeメソッドがサポートされていないために必要になります。
     * 
     * @param element
     *            WebElement
     * @param attr
     *            属性名
     * @param value
     *            属性値
     * @return WebElementが属性を持っている場合にtrue
     */
    private boolean hasAttribute(WebElement element, String attr, String value) {
        try {
            return value.equals(element.getAttribute(attr));
        } catch (Exception e) {
            return false;
        }
    }
}
