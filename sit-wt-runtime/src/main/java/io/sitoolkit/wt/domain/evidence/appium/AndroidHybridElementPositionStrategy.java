package io.sitoolkit.wt.domain.evidence.appium;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.appium.java_client.AppiumDriver;
import io.sitoolkit.wt.domain.evidence.ElementPositionStrategy;
import io.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport2;

public class AndroidHybridElementPositionStrategy implements ElementPositionStrategy {

  @Override
  public void init(ElementPositionSupport2 eps, WebDriver driver) {
    AppiumDriver<WebElement> appiumDriver = (AppiumDriver<WebElement>) driver;
    String context = appiumDriver.getContext();

    // BasePosition
    appiumDriver.context("NATIVE_APP");
    WebElement baseElement = appiumDriver.findElementByClassName("android.webkit.WebView");
    eps.setBasePosition(baseElement.getLocation());
    Dimension dim = baseElement.getSize();
    appiumDriver.context(context);

    // Scale
    WebElement htmlElement = appiumDriver.findElementByTagName("body");
    double scale = (double) dim.getWidth() / htmlElement.getSize().getWidth();
    eps.setScale(scale);

  }

  @Override
  public Point getCurrentBasePosition(WebDriver driver, WebElement currentFrame) {
    return new Point(0, 0);
  }

}
