package io.sitoolkit.wt.domain.evidence.selenium;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.sitoolkit.wt.domain.evidence.ScreenshotTaker;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.selenium.WebDriverUtils;

public class SeleniumScreenshotTaker extends ScreenshotTaker {

    @Resource
    TestContext current;

    @Resource
    WebDriver driver;

    @Resource
    TakesScreenshot takesScreenshot;

    @Resource
    PropertyManager pm;

    private boolean resizeWindow = false;

    private Robot robot;

    private long waitTimeout;

    private static final long EDGE_SCROLL_WAIT_MS = 50L;

    @PostConstruct
    public void init() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            log.warn("warn", e);
        }

        waitTimeout = pm.getImplicitlyWait() / 1000;
    }

    @Override
    protected String getAsData() {

        Dimension orgSize = null;

        if (resizeWindow) {
            orgSize = driver.manage().window().getSize();
            Dimension bodySize = driver.findElement(By.tagName("body")).getSize();
            driver.manage().window().setSize(bodySize);
            log.debug("bodySize {}", bodySize);
        }

        // Because MS Drivers(IE, Edge) ClickOperation is emulated by JS click
        // function,
        // we have to wait for finish to navigate and load page.
        // See also
        // io.sitoolkit.wt.domain.operation.selenium.SeleniumOperation#click
        if (pm.isMsDriver() || pm.isFirefoxDriver()) {
            new WebDriverWait(driver, waitTimeout).until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        return ((JavascriptExecutor) driver)
                                .executeScript("return document.readyState;").equals("complete");
                    } catch (WebDriverException e) {
                        log.debug("this exception is no effect for testing", e);
                        return true;
                    }
                }
            });
        }

        String data = takesScreenshot.getScreenshotAs(OutputType.BASE64);

        if (orgSize != null) {
            driver.manage().window().setSize(orgSize);
        }

        return data;
    }

    @Override
    protected String getDialogAsData() {
        if (robot == null) {
            return null;
        }

        Rectangle windowRect = current.getWindowRect();
        if (windowRect == null || windowRect.isEmpty()) {
            return null;
        }

        new WebDriverWait(driver, pm.getDialogWaitInSecond())
                .until(ExpectedConditions.alertIsPresent());

        try {
            BufferedImage img = robot.createScreenCapture(windowRect);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            current.setWindowRect(null);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            log.warn("screenshot.get.error", e);
            return null;
        }
    }

    @Override
    protected WindowSize getWindowSize() {
        int pageHeight = Integer.parseInt(String.valueOf(
                WebDriverUtils.executeScript(driver, "return document.body.scrollHeight")));
        int pageWidth = Integer.parseInt(String
                .valueOf(WebDriverUtils.executeScript(driver, "return document.body.scrollWidth")));

        int windowHeight = Integer.parseInt(String.valueOf(WebDriverUtils.executeScript(driver,
                "return document.documentElement.clientHeight")));
        int windowWidth = Integer.parseInt(String.valueOf(WebDriverUtils.executeScript(driver,
                "return document.documentElement.clientWidth")));

        return new WindowSize(pageHeight, pageWidth, windowHeight, windowWidth);
    }

    @Override
    protected File getAsFile() {
        return takesScreenshot.getScreenshotAs(OutputType.FILE);
    }

    @Override
    protected void scrollTo(int x, int y) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(" + x + ", " + y + ");");

        String driverType = StringUtils.defaultString(pm.getDriverType());

        if (!"edge".equals(driverType)) {
            return;
        }

        try {
            Thread.sleep(EDGE_SCROLL_WAIT_MS);
        } catch (InterruptedException e) {
            log.warn("thread.sleep.error", e);
        }
    }

    public void setResizeWindow(boolean resizeWindow) {
        this.resizeWindow = resizeWindow;
    }

}
