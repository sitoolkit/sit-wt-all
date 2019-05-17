package io.sitoolkit.wt.domain.evidence.selenium;

import org.openqa.selenium.WebDriver;
import io.sitoolkit.wt.domain.evidence.ElementPositionStrategy;

public class ElementPositionSupportFactoryPc {

  public static ElementPositionStrategy getStrategy(WebDriver driver) {
    return new PcBrowserElementPositionStrategy();
  }
}
