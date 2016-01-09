/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.domain.evidence.selenium;

import javax.annotation.Resource;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.appium.java_client.AppiumDriver;

/**
 *
 * @author yuichi.kuwahara
 */
public class ElementPositionSupport {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    protected WebDriver driver;
    
    private WebElement currentFrame;

    public WebElement getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(WebElement currentFrame) {
        this.currentFrame = currentFrame;
    }

    /**
     * 要素位置の基準となる座標を取得します。
     * 
     * @return 要素位置の基準となる座標
     */
    protected Point getCurrentBasePosition() {
        if (currentFrame == null) {
            return driver.findElement(By.tagName("html")).getLocation();
        } else {
            driver.switchTo().defaultContent();
            Point documentPos = driver.findElement(By.tagName("html")).getLocation();
            Point framePos = currentFrame.getLocation();
            driver.switchTo().frame(currentFrame);
            return new Point(
                    documentPos.getX() - framePos.getX(),
                    documentPos.getY() - framePos.getY());
        }
    }

    public ElementPosition get(WebElement element) {
        if (element == null) {
            return ElementPosition.EMPTY;
        } else if (driver instanceof TakesScreenshot) {
            Point elementPos = element.getLocation();
            Point basePos = getCurrentBasePosition();

            log.debug("要素:{}, 要素位置:{}, 基準位置:{}",
                    new Object[]{element, elementPos, basePos});

            return new ElementPosition(
                    elementPos.getX() - basePos.getX(),
                    elementPos.getY() - basePos.getY(),
                    element.getSize().getWidth(),
                    element.getSize().getHeight());
        } else {
            return ElementPosition.EMPTY;
        }
    }
}
