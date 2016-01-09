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
package org.sitoolkit.wt.domain.tester.selenium;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.sitoolkit.wt.domain.tester.Tester;
import org.sitoolkit.wt.infra.PropertyManager;

/**
 *
 * @author yuichi.kuwahara
 */
public class SeleniumTester extends Tester {

    @Resource
    WebDriver seleniumDriver;

    @Resource
    PropertyManager pm;

    @PostConstruct
    public void postConstruct() {
        log.info("WebDriverを起動します ", seleniumDriver);
    }

    @Override
    public void setUpClass(String testScriptPath, String sheetName) {
        super.setUpClass(testScriptPath, sheetName);
        seleniumDriver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(),
                TimeUnit.MILLISECONDS);
        // TODO mobile系はwindow.setSizeできない
        Dimension windowSize = new Dimension(pm.getWindowWidth(), pm.getWindowHeight());
        try {
            seleniumDriver.manage().window().setSize(windowSize);
        } catch (WebDriverException e) {
            // NOP
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("WebDriverを停止します {}", seleniumDriver);
        try {
            // TODO 停止のタイミングはここではない。
            seleniumDriver.quit();
        } catch (Exception e) {
            log.warn("WebDriverの停止で例外が発生しました。{}", e.getMessage());
        }
    }
}
