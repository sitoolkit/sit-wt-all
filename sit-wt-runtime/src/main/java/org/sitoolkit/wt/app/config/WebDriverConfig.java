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

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.sitoolkit.wt.domain.tester.TestEventListener;
import org.sitoolkit.wt.domain.tester.selenium.TestEventListenerWebDriverImpl;
import org.sitoolkit.wt.infra.firefox.FirefoxManager;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.sitoolkit.wt.infra.selenium.WebDriverCloser;
import org.sitoolkit.wt.infra.selenium.WebDriverInstaller;
import org.sitoolkit.wt.infra.selenium.WebDriverMethodInterceptor;
import org.sitoolkit.wt.infra.selenium.WebElementExceptionChecker;
import org.sitoolkit.wt.infra.selenium.WebElementExceptionCheckerImpl;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
@Import({ PCWebDriverConfig.class, MobileWebDriverConfig.class })
public class WebDriverConfig {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(WebDriverConfig.class);

    @Bean
    public WebDriverInstaller webDriverInstaller() {
        return new WebDriverInstaller();
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

        LOG.debug("webdriver.proxy", proxy);

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
