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
import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.sitoolkit.wt.domain.evidence.OperationLog;
import org.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport2;
import org.sitoolkit.wt.domain.operation.Operation;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.infra.ElementNotFoundException;
import org.sitoolkit.wt.infra.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yuichi.kuwahara
 */
public abstract class SeleniumOperation implements Operation {

    private int waitTimes = 20;
    private int waitSpan = 250;

    @Resource
    PropertyManager pm;

    protected Logger log = LoggerFactory.getLogger(getClass());
    @Resource
    OperationLog opelog;
    @Resource
    protected WebDriver seleniumDriver;
    @Resource
    ElementPositionSupport2 position;

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
            WebElement element = seleniumDriver.findElement(by(locator));

            setElementVisible(element);
            return element;
        } catch (NoSuchElementException e) {
            throw ElementNotFoundException.create(locator, e);
        }
    }

    protected void setElementVisible(WebElement element) {
        if (seleniumDriver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) seleniumDriver)
                    .executeScript("return arguments[0].style.display;", element);
        }
    }

    protected List<WebElement> findElements(Locator locator) {
        try {

            List<WebElement> elements = seleniumDriver.findElements(by(locator));
            for (WebElement element : elements) {
                setElementVisible(element);
            }

            return elements;
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
        log.debug("checkElement:{}, clickElement:{}, checked:{}", checkElement, clickElement,
                checked);
        if (checkElement.isSelected() != checked) {
            clickElement.click();
            return true;
        }
        return false;
    }

    protected void info(String verb, WebElement element) {
        ElementPosition pos = position.get(element);
        opelog.info(log, verb, pos);
    }

    protected void info(String object, String verb, WebElement element) {
        ElementPosition pos = position.get(element);
        opelog.info(log, object, verb, pos);
    }

    public void info(WebElement element, String messagePattern, Object... params) {
        ElementPosition pos = position.get(element);
        opelog.info(log, pos, messagePattern, params);
    }

    protected void addPosition(WebElement element) {
        opelog.addPosition(position.get(element));
    }

    public int getWaitTimes() {
        return waitTimes;
    }

    public void setWaitTimes(int waitTimes) {
        this.waitTimes = waitTimes;
    }

    public int getWaitSpan() {
        return waitSpan;
    }

    public void setWaitSpan(int waitSpan) {
        this.waitSpan = waitSpan;
    }

}
