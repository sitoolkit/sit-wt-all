package io.sitoolkit.wt.domain.evidence.selenium;

import org.openqa.selenium.WebDriver;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.sitoolkit.wt.domain.evidence.ElementPositionStrategy;
import io.sitoolkit.wt.domain.evidence.appium.AndroidHybridElementPositionStrategy;
import io.sitoolkit.wt.domain.evidence.appium.IOSHybridElementPositionStrategy;
import io.sitoolkit.wt.domain.evidence.appium.MobileNativeElementPositionStartegy;

public class ElementPositionSupportFactoryMobile {

  public static ElementPositionStrategy getStrategy(WebDriver driver) {

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
  }
}
