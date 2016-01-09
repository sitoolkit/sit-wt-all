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
package org.sitoolkit.wt.domain.operation.selenium;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.operation.ScreenshotOperation;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.infra.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class SeleniumScreenshotOperation implements ScreenshotOperation {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumScreenshotOperation.class);

    protected Robot robot;

    @Resource
    WebDriver seleniumDriver;

    @Resource
    TestContext current;

    @Resource
    PropertyManager pm;

    /**
     * ダイアログが表示されるまでの待機時間(ミリ秒)
     */
    private int dialogWaitSpan = 500;

    public SeleniumScreenshotOperation() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            LOG.warn("", e);
        }
    }

    @Override
    public File get() {
        if (seleniumDriver instanceof TakesScreenshot) {
            if (pm.isResizeWindow()) {
                Dimension bodySize = seleniumDriver.findElement(By.tagName("body")).getSize();
                seleniumDriver.manage().window().setSize(bodySize);
                LOG.debug("bodySize {}", bodySize);
            }
            return ((TakesScreenshot) seleniumDriver).getScreenshotAs(OutputType.FILE);
        } else {
            LOG.warn("ドライバ{}はスクリーンショットを取得できません。", seleniumDriver.getClass().getName());
            return null;
            // try {
            // File file = File.createTempFile("sit-wt", "html");
            // FileUtils.write(file, seleniumDriver.getPageSource(), "utf-8");
            // return file;
            // } catch (IOException e) {
            // throw new TestException(e);
            // }
        }
    }

    @Override
    public File getWithDialog() {
        if (robot == null) {
            // TODO スクリーンショット失敗用の画像を返す
            return null;
        }
        Rectangle windowRect = current.getWindowRect();
        if (windowRect == null || windowRect.isEmpty()) {
            return null;
        }
        try {
            File file = File.createTempFile("sit-wt", "");
            try {
                // TODO 待機＋タイムアウト
                Thread.sleep(getDialogWaitSpan());
            } catch (InterruptedException e) {
                LOG.warn("スレッドの待機に失敗");
            }
            BufferedImage img = robot.createScreenCapture(windowRect);
            ImageIO.write(img, "png", file);
            current.setWindowRect(null);
            return file;
        } catch (IOException e) {
            LOG.warn("スクリーンショットの取得に失敗しました", e);
            // TODO スクリーンショット失敗用の画像を返す
            return null;
        }
    }

    public int getDialogWaitSpan() {
        return dialogWaitSpan;
    }

    public void setDialogWaitSpan(int dialogWaitSpan) {
        this.dialogWaitSpan = dialogWaitSpan;
    }

}
