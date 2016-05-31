package org.sitoolkit.wt.app.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.sitoolkit.wt.domain.tester.selenium.WebDriverCloser;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

@Configuration
public class WebDriverConfig {

    @Bean
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "thread")
    public RemoteWebDriver webDriver(PropertyManager pm, WebDriverCloser closer) {
        RemoteWebDriver webDriver = null;

        String driverType = pm.getDriverType();
        DesiredCapabilities capabilities = new DesiredCapabilities();

        Map<String, String> map = PropertyUtils.loadAsMap("/capabilities", true);

        for (Entry<String, String> entry : map.entrySet()) {
            capabilities.setCapability(entry.getKey(), entry.getValue());
        }

        if (driverType == null) {

            webDriver = new FirefoxDriver(capabilities);

        } else {

            switch (driverType) {

                case "chrome":
                    webDriver = new ChromeDriver(capabilities);
                    break;

                case "ie":
                    webDriver = new InternetExplorerDriver(capabilities);
                    break;

                case "safari":
                    webDriver = new SafariDriver(capabilities);
                    break;

                case "android":
                    webDriver = new AndroidDriver<>(pm.getAppiumAddress(), capabilities);
                    break;

                case "ios":
                    webDriver = new IOSDriver<>(pm.getAppiumAddress(), capabilities);
                    break;

                default:
                    webDriver = new FirefoxDriver(capabilities);
            }

        }

        webDriver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(), TimeUnit.MILLISECONDS);

        if (!(webDriver instanceof AppiumDriver<?>)) {
            Dimension windowSize = new Dimension(pm.getWindowWidth(), pm.getWindowHeight());
            webDriver.manage().window().setSize(windowSize);
        }

        closer.register(webDriver);

        return webDriver;
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
}
