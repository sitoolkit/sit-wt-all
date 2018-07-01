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

import java.util.List;

import javax.annotation.Resource;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport2;
import org.sitoolkit.wt.domain.operation.Operation;
import org.sitoolkit.wt.domain.operation.OperationResult;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.ElementNotFoundException;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;

/**
 *
 * @author yuichi.kuwahara
 */
public abstract class SeleniumOperation implements Operation {

    @Resource
    PropertyManager pm;

    protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

    @Resource
    protected WebDriver seleniumDriver;

    @Resource
    ElementPositionSupport2 position;

    @Override
    public OperationResult operate(TestStep testStep) {

        SeleniumOperationContext ctx = new SeleniumOperationContext();
        ctx.setLogger(log);
        ctx.setTestStep(testStep);
        ctx.setElementPositionSupport(position);

        execute(testStep, ctx);

        OperationResult result = new OperationResult();
        result.setRecords(ctx.getRecords());

        return result;
    }

    protected abstract void execute(TestStep testStep, SeleniumOperationContext ctx);

    protected By by(Locator locator) {
        switch (locator.getTypeVo()) {
            case css:
                return By.cssSelector(locator.getValue());
            case name:
                return By.name(locator.getValue());
            case xpath:
                return By.xpath(locator.getValue());
            case link:
                return By.linkText(locator.getValue());
            case tag:
                return By.tagName(locator.getValue());
            default:
                return By.id(locator.getValue());
        }
    }

    protected WebElement findElement(Locator locator) {
        try {
            return seleniumDriver.findElement(by(locator));
        } catch (NoSuchElementException e) {
            throw ElementNotFoundException.create(locator, e);
        }
    }

    protected List<WebElement> findElements(Locator locator) {
        try {
            return seleniumDriver.findElements(by(locator));
        } catch (NoSuchElementException e) {
            throw ElementNotFoundException.create(locator, e);
        }
    }

    /**
     * WebElementのチェック状態を設定します。
     *
     * @param checkElement
     *            WebElement
     * @param checked
     *            チェック状態
     */
    protected boolean setChecked(WebElement checkElement, WebElement clickElement,
            boolean checked) {
        log.debug("check.element", checkElement, clickElement, checked);
        if (checkElement.isSelected() != checked) {
            click(clickElement);
            return true;
        }
        return false;
    }

    protected void click(WebElement element) {
        if (pm.isEdgeDriver()) {
            JavascriptExecutor jse = (JavascriptExecutor) seleniumDriver;
            jse.executeScript("arguments[0].click();", element);

        } else {
            element.click();
        }
    }

    protected void input(WebElement element, String value) {
        if (pm.isEdgeDriver()) {
            JavascriptExecutor jse = (JavascriptExecutor) seleniumDriver;
            jse.executeScript("arguments[0].value = arguments[1];", element, value);

        } else {
            element.sendKeys(value);
        }
    }
}
