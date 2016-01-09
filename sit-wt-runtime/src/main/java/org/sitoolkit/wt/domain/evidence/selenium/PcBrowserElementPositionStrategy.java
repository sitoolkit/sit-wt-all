package org.sitoolkit.wt.domain.evidence.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.ElementPositionStrategy;

public class PcBrowserElementPositionStrategy implements ElementPositionStrategy {

    @Override
    public void init(ElementPositionSupport2 eps, WebDriver driver) {
        eps.setBasePosition(getCurrentBasePosition(driver, null));
    }

    @Override
    public Point getCurrentBasePosition(WebDriver driver, WebElement currentFrame) {
        if (currentFrame == null) {
            return driver.findElement(By.tagName("html")).getLocation();
        } else {
            driver.switchTo().defaultContent();
            Point documentPos = driver.findElement(By.tagName("html")).getLocation();
            Point framePos = currentFrame.getLocation();
            driver.switchTo().frame(currentFrame);
            return new Point(documentPos.getX() - framePos.getX(),
                    documentPos.getY() - framePos.getY());
        }
    }

}
