package org.sitoolkit.wt.domain.evidence.selenium;

import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.evidence.ElementPositionStrategy;

public class ElementPositionSupportFactoryPc {

    public static ElementPositionStrategy getStrategy(WebDriver driver) {
        return new PcBrowserElementPositionStrategy();
    }
}
