package org.sitoolkit.wt.domain.evidence.selenium;

import javax.annotation.Resource;

import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.sitoolkit.wt.domain.evidence.ElementPositionStrategy;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;

public class ElementPositionSupport2 {

    protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

    private boolean frameChanged = false;

    private double scale = 1;

    private Point basePosition;

    private WebElement currentFrame;

    private ElementPositionStrategy strategy;

    @Resource
    private WebDriver driver;

    public ElementPosition get(WebElement element) {
        if (element == null || !(driver instanceof TakesScreenshot)) {
            return ElementPosition.EMPTY;
        }

        if (strategy == null) {
            String driverType = System.getProperty("driver.type");
            strategy = ("android".equals(driverType) || "ios".equals(driverType))
                    ? ElementPositionSupportFactoryMobile.getStrategy(driver)
                    : ElementPositionSupportFactoryPc.getStrategy(driver);
        }

        if (basePosition == null) {
            strategy.init(this, driver);
        }

        Point elementPos = element.getLocation();

        if (frameChanged) {
            basePosition = strategy.getCurrentBasePosition(driver, currentFrame);
            frameChanged = false;
        }

        log.debug("element.position", new Object[] { element, elementPos, basePosition });

        return new ElementPosition(elementPos.getX() * scale - basePosition.getX(),
                elementPos.getY() * scale - basePosition.getY(),
                element.getSize().getWidth() * scale, element.getSize().getHeight() * scale);
    }

    public void setCurrentFrame(WebElement currentFrame) {
        this.currentFrame = currentFrame;
        this.frameChanged = true;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setBasePosition(Point basePosition) {
        this.basePosition = basePosition;
    }

}
