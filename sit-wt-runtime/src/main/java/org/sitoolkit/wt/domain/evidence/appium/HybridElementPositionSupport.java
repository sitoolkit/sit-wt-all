package org.sitoolkit.wt.domain.evidence.appium;

import org.apache.velocity.runtime.parser.node.MathUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport;

import io.appium.java_client.AppiumDriver;

public class HybridElementPositionSupport extends ElementPositionSupport {

    private Point basePos;
  
    private double scale;
    
    private boolean initialized;
    
    
    @Override
    public ElementPosition get(WebElement element) {
        if (!initialized) {
            AppiumDriver<WebElement> appiumDriver = (AppiumDriver<WebElement>)driver;
            String context = appiumDriver.getContext();

            appiumDriver.context("NATIVE_APP");

            WebElement baseElement = appiumDriver.findElementByClassName("android.webkit.WebView");
            basePos = baseElement.getLocation();
            Dimension dim = baseElement.getSize();
            
            appiumDriver.context(context);
            
            WebElement htmlElement = appiumDriver.findElementByTagName("body");
            
            scale = (double)dim.getWidth() / htmlElement.getSize().getWidth();

            initialized = true;
            log.debug("座標を初期化しました 基準座標:{}, 縮尺:{}", basePos, scale);
        }

        Point elementPos = element.getLocation();
        log.debug("要素:{}, 要素位置:{}", element, elementPos);
        
        return new ElementPosition(
                elementPos.getX() * scale + basePos.getX(),
                elementPos.getY() * scale + basePos.getY(),
                element.getSize().getWidth() * scale,
                element.getSize().getHeight() * scale);
    }
    
}
