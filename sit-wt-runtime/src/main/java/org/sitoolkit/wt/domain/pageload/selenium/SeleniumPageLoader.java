package org.sitoolkit.wt.domain.pageload.selenium;

import javax.annotation.Resource;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.ElementPosition;

public abstract class SeleniumPageLoader {

    @Resource
    protected WebDriver driver;

    protected ElementPosition convert(Point point) {
        return new ElementPosition(point.x, point.y, 0, 0);
    }

    protected WebElement findLabelByForId(String forId) {
        try {
            return driver.findElement(By.cssSelector("label[for='" + forId + "']"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    // @PreDestroy
    // public void preDestroy() {
    // driver.quit();
    // }
}
