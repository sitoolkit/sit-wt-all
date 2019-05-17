package io.sitoolkit.wt.domain.evidence.appium;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.sitoolkit.wt.domain.evidence.ElementPositionStrategy;
import io.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport2;

public class MobileNativeElementPositionStartegy implements ElementPositionStrategy {

  @Override
  public void init(ElementPositionSupport2 eps, WebDriver driver) {
    // NOP
  }

  @Override
  public Point getCurrentBasePosition(WebDriver driver, WebElement currentFrame) {
    return new Point(0, 0);
  }

}
