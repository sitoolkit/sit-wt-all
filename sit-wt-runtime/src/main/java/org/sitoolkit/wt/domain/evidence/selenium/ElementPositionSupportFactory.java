package org.sitoolkit.wt.domain.evidence.selenium;

import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.evidence.ElementPositionStrategy;
import org.sitoolkit.wt.domain.evidence.appium.AndroidHybridElementPositionStrategy;
import org.sitoolkit.wt.domain.evidence.appium.IOSHybridElementPositionStrategy;
import org.sitoolkit.wt.domain.evidence.appium.MobileNativeElementPositionStartegy;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;

public class ElementPositionSupportFactory {

    public static ElementPositionStrategy getStrategy(WebDriver driver) {

        if (driver instanceof AppiumDriver<?>) {
            AppiumDriver<?> appiumDriver = (AppiumDriver<?>) driver;
            if (appiumDriver.getContext().startsWith("WEBVIEW")) {
                if (appiumDriver instanceof IOSDriver<?>) {
                    return new IOSHybridElementPositionStrategy();
                } else {
                    return new AndroidHybridElementPositionStrategy();
                }
            } else {
                return new MobileNativeElementPositionStartegy();
            }
        } else {
            return new PcBrowserElementPositionStrategy();
        }
    }
}