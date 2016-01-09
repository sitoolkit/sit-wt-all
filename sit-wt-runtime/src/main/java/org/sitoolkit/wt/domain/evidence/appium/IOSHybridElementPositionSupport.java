package org.sitoolkit.wt.domain.evidence.appium;

import java.util.Set;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport;

import io.appium.java_client.AppiumDriver;

public class IOSHybridElementPositionSupport extends ElementPositionSupport {

	private Point basePos;

    private double scale;
    
    private boolean initialized;
    
    public IOSHybridElementPositionSupport() {
		super();
	}

	public IOSHybridElementPositionSupport(double scale) {
    	super();
    	this.scale = scale;
    }

	@Override
    public ElementPosition get(WebElement element) {
        if (!initialized) {
            AppiumDriver<WebElement> appiumDriver = (AppiumDriver<WebElement>)driver;
            
            appiumDriver.context("NATIVE_APP");
            WebElement baseElement = appiumDriver.findElementByClassName("UIAWebView");
            basePos = baseElement.getLocation();
            
            Set<String> contextNames = appiumDriver.getContextHandles();
            appiumDriver.context((String) contextNames.toArray()[1]);
            
            initialized = true;
            log.debug("座標を初期化しました 基準座標:{}, 縮尺:{}", basePos, scale);
        }
        
        Point elementPos = element.getLocation();
        
        return new ElementPosition(
                elementPos.getX() * scale + basePos.getX(),
                elementPos.getY() * scale + basePos.getY(),
                element.getSize().getWidth() * scale,
                element.getSize().getHeight() * scale);
    }
    
}
