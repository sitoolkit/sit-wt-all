/*
 * Copyright 2016 Monocrea Inc.
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
package org.sitoolkit.wt.infra.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.app.config.RuntimeConfig;
import org.sitoolkit.wt.app.config.WebDriverConfig;
import org.sitoolkit.wt.domain.tester.selenium.WebDriverCloser;
import org.sitoolkit.wt.infra.ApplicationContextHelper;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.SitPathUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuntimeConfig.class)
public class WebElementMethodInterceptorTest {

    // @Resource
    // WebDriver webDriver;
    //
    PropertyManager pm;

    @Before
    public void setUp() {
        pm = ApplicationContextHelper.getBean(PropertyManager.class);
    }

    /**
     * 通常の{@code WebDriver}で{@link #operate(WebDriver)}を実行し、
     * {@code StaleElementReferenceException}が送出されるケース
     *
     * @throws MalformedURLException
     */
    @Test(expected = StaleElementReferenceException.class)
    public void testStaleElementNormalWebDriver() throws MalformedURLException {

        WebDriver normalWebDriver = getNormalWebDriver();
        try {
            operate(normalWebDriver);
            fail();
        } finally {
            normalWebDriver.close();
        }

    }

    private WebDriver getNormalWebDriver() throws MalformedURLException {
        WebDriverConfig config = new WebDriverConfig();
        return config.innerWebDriver(ApplicationContextHelper.getBean(PropertyManager.class),
                ApplicationContextHelper.getBean(WebDriverCloser.class));
    }

    /**
     * 再実行機能付き{@code WebDriver}で{@link #operate(WebDriver)}を実行し、
     * {@code StaleElementReferenceException}が送出されるが再実行により正常終了するケース
     */
    @Test
    public void testStaleElementRetriableWebDriver() {
        WebDriver webDriver = ApplicationContextHelper.getBean(WebDriver.class);
        operate(webDriver);
    }

    /**
     *
     * @param webDriver
     */
    void operate(WebDriver webDriver) {
        webDriver.get(SitPathUtils.buildUrl(pm.getBaseUrl(), "retry.html"));
        WebElement btn = webDriver.findElement(By.id("rewriteBtn"));
        WebElement txt = webDriver.findElement(By.id("rewritedTxt"));
        click(webDriver, btn); // このタイミングでretry.htmlではtxtのDOMが書き換えられる。
        assertThat("", txt.getAttribute("value"), is("rewrited"));

        click(webDriver, btn); // このタイミングで再度retry.htmlではtxtのDOMが書き換えられる。
        assertThat("", txt.getAttribute("value"), is("rewrited"));
    }

    /**
     * 通常の{@code WebDriver}で非表示項目を操作し{@code ElementNotVisibleException}
     * が送出されるケース
     *
     * @throws MalformedURLException
     */
    @Test(expected = ElementNotVisibleException.class)
    public void testHiddenWithNormalWebDriver() throws MalformedURLException {

        if (pm.isEdgeDriver()) {
            throw new ElementNotVisibleException("EdgeはWebElement.clickが動作しないためテストできない");
        }

        WebDriver normalWebDriver = getNormalWebDriver();
        try {
            operate2(normalWebDriver);
            fail();
        } finally {
            normalWebDriver.close();
        }

    }

    /**
     * 再実行付き{@code WebDriver}で非表示項目を操作し{@code ElementNotVisibleException}
     * が送出されるが再実行により正常終了するケース
     */
    @Test
    public void testHiddenWithRetriableWebDriver() {
        WebDriver webDriver = ApplicationContextHelper.getBean(WebDriver.class);
        operate2(webDriver);
    }

    void operate2(WebDriver webDriver) {
        webDriver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);

        try {
            webDriver.get(SitPathUtils.buildUrl(pm.getBaseUrl(), "retry.html"));
            click(webDriver, webDriver.findElement(By.id("appearBtn")));
            click(webDriver, webDriver.findElement(By.id("hiddenBtn")));
        } finally {
            webDriver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(),
                    TimeUnit.MILLISECONDS);
        }

    }

    private void click(WebDriver driver, WebElement element) {
        if (pm.isEdgeDriver()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
        } else if (pm.isIEDriver()) {
            element.sendKeys(Keys.SPACE);
        } else {
            element.click();
        }
    }
}
