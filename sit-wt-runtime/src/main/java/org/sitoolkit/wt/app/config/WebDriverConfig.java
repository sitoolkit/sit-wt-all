package org.sitoolkit.wt.app.config;

import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

@Configuration
public class WebDriverConfig {

    @Bean
    public WebDriver webDriver(PropertyManager pm) {
        WebDriver webDriver = null;

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

        return webDriver;
    }

}
