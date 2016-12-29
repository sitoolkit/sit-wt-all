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
package org.sitoolkit.wt.app.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;
import org.sitoolkit.wt.domain.tester.TestEventListener;
import org.sitoolkit.wt.domain.tester.selenium.TestEventListenerWebDriverImpl;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.firefox.FirefoxManager;
import org.sitoolkit.wt.infra.selenium.WebDriverCloser;
import org.sitoolkit.wt.infra.selenium.WebDriverInstaller;
import org.sitoolkit.wt.infra.selenium.WebDriverMethodInterceptor;
import org.sitoolkit.wt.infra.selenium.WebElementExceptionChecker;
import org.sitoolkit.wt.infra.selenium.WebElementExceptionCheckerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

@Configuration
public class WebDriverConfig {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverConfig.class);

    private int windowShiftLeft = 0;

    private int windowShiftTop = 0;

    @Bean
    public WebDriverInstaller webDriverInstaller() {
        return new WebDriverInstaller();
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "thread")
    public RemoteWebDriver innerWebDriver(PropertyManager pm, WebDriverCloser closer,
            WebDriverInstaller webDriverInstaller, FirefoxManager firefoxManager)
            throws MalformedURLException {
        RemoteWebDriver webDriver = null;

        String driverType = StringUtils.defaultString(pm.getDriverType());
        DesiredCapabilities capabilities = new DesiredCapabilities();

        Map<String, String> map = pm.getCapabilities();

        for (Entry<String, String> entry : map.entrySet()) {
            capabilities.setCapability(entry.getKey(), entry.getValue());
        }

        LOG.info("WebDriverを起動します driverType:{}, capabilities:{}", driverType, capabilities);

        switch (driverType) {

            case "chrome":
                webDriverInstaller.installChromeDriver();
                webDriver = new ChromeDriver(capabilities);
                break;

            case "ie":
            case "internet explorer":
                webDriverInstaller.installIeDriver();
                webDriver = new InternetExplorerDriver(capabilities);
                break;

            case "edge":
                webDriverInstaller.installEdgeDriver();
                webDriver = new EdgeDriver(capabilities);
                break;

            case "safari":
                try {
                    webDriver = new SafariDriver(capabilities);
                } catch (UnreachableBrowserException e) {
                    if (StringUtils.startsWith(e.getMessage(),
                            "Failed to connect to SafariDriver")) {
                        webDriverInstaller.installSafariDriver();
                        webDriver = new SafariDriver(capabilities);
                    }
                }
                break;

            case "remote":
                LOG.info("RemoteWebDriverの接続先:{}", pm.getHubUrl());
                webDriver = new RemoteWebDriver(new URL(pm.getHubUrl()), capabilities);

                break;

            case "android":
                LOG.info("AndroidDriverの接続先:{}", pm.getAppiumAddress());
                webDriver = new AndroidDriver<>(pm.getAppiumAddress(), capabilities);
                break;

            case "ios":
                LOG.info("IOSDriverの接続先:{}", pm.getAppiumAddress());
                webDriver = new IOSDriver<>(pm.getAppiumAddress(), capabilities);
                break;

            default: // include firefox
                // geckodriver is not stable yet as of 2016/10
                // so we doesn't support neigther selenium 3 nor firefox 48.x
                // higher
                // webDriverInstaller.installGeckoDriver();

                webDriver = firefoxManager.startWebDriver(capabilities);
        }

        webDriver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(), TimeUnit.MILLISECONDS);

        if (!(webDriver instanceof AppiumDriver<?>)) {
            webDriver.manage().window().setPosition(new Point(pm.getWindowLeft() + windowShiftLeft,
                    pm.getWindowTop() + windowShiftTop));
            windowShiftTop += pm.getWindowShiftTop();
            windowShiftLeft += pm.getWindowShiftLeft();

            Dimension windowSize = new Dimension(pm.getWindowWidth(), pm.getWindowHeight());
            webDriver.manage().window().setSize(windowSize);
        }

        closer.register(webDriver);

        LOG.debug("init webDriver:{}", webDriver);

        return webDriver;
    }

    @Bean(destroyMethod = "")
    @Primary
    public RemoteWebDriver webDriver(RemoteWebDriver webDriver,
            WebElementExceptionChecker checker) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(webDriver.getClass());
        proxyFactory.addAdvice(new WebDriverMethodInterceptor(checker));
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setTarget(webDriver);

        Object proxy = proxyFactory.getProxy();

        LOG.debug("proxy webDriver:{}", proxy);

        return (RemoteWebDriver) proxy;
    }

    @Bean
    public WebElementExceptionChecker checker() {
        return new WebElementExceptionCheckerImpl();
    }

    @Bean
    public WebDriverCloser webDriverCloser() {
        return new WebDriverCloser();
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.INTERFACES, scopeName = "thread")
    public TakesScreenshot takesScreenshot(WebDriver webDriver) {
        return (TakesScreenshot) webDriver;
    }

    @Bean
    public TestEventListener testEventListener() {
        return new TestEventListenerWebDriverImpl();
    }

    @Bean
    public FirefoxManager firefoxManager() {
        return new FirefoxManager();
    }

}
