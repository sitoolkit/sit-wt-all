package io.sitoolkit.wt.domain.evidence;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport2;

public interface ElementPositionStrategy {

    void init(ElementPositionSupport2 eps, WebDriver driver);

    Point getCurrentBasePosition(WebDriver driver, WebElement currentFrame);
}
