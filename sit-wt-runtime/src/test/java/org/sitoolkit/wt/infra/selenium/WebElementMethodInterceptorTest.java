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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.sitoolkit.wt.app.config.RuntimeConfig;
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
    // @Resource
    // PropertyManager pm;

    /**
     * 通常の{@code WebDriver}で{@link #operate(WebDriver)}を実行し、
     * {@code StaleElementReferenceException}が送出されるケース
     */
    @Test(expected = StaleElementReferenceException.class)
    public void testStaleElementNormalWebDriver() {

        WebDriver normalWebDriver = new FirefoxDriver();
        try {
            operate(normalWebDriver);
            fail();
        } finally {
            normalWebDriver.close();
        }

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
        PropertyManager pm = ApplicationContextHelper.getBean(PropertyManager.class);
        webDriver.get(SitPathUtils.buildUrl(pm.getBaseUrl(), "retry.html"));
        WebElement btn = webDriver.findElement(By.id("rewriteBtn"));
        WebElement txt = webDriver.findElement(By.id("rewritedTxt"));
        btn.click(); // このタイミングでretry.htmlではtxtのDOMが書き換えられる。
        assertThat("", txt.getAttribute("value"), is("rewrited"));
    }

    /**
     * 通常の{@code WebDriver}で非表示項目を操作し{@code ElementNotVisibleException}
     * が送出されるケース
     */
    @Test(expected = ElementNotVisibleException.class)
    public void testHiddenWithNormalWebDriver() {

        WebDriver normalWebDriver = new FirefoxDriver();
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
        PropertyManager pm = ApplicationContextHelper.getBean(PropertyManager.class);

        webDriver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);

        try {
            webDriver.get(SitPathUtils.buildUrl(pm.getBaseUrl(), "retry.html"));
            webDriver.findElement(By.id("appearBtn")).click();
            webDriver.findElement(By.id("hiddenBtn")).click();
        } finally {
            webDriver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(),
                    TimeUnit.MILLISECONDS);
        }

    }
}
