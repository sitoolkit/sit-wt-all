package org.sitoolkit.wt.domain.evidence.appium;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.ElementPositionStrategy;
import org.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport2;

import io.appium.java_client.AppiumDriver;

public class IOSHybridElementPositionStrategy implements ElementPositionStrategy {

    @SuppressWarnings("unchecked")
    @Override
    public void init(ElementPositionSupport2 eps, WebDriver driver) {
        AppiumDriver<WebElement> appiumDriver = (AppiumDriver<WebElement>) driver;

        String context = appiumDriver.getContext();

        appiumDriver.context("NATIVE_APP");
        WebElement baseElement = appiumDriver.findElementByClassName("UIAWebView");
        eps.setBasePosition(baseElement.getLocation());

        appiumDriver.context(context);

    }

    @Override
    public Point getCurrentBasePosition(WebDriver driver, WebElement currentFrame) {
        return new Point(0, 0);
    }

}
