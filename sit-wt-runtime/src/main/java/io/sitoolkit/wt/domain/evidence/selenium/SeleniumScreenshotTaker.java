package io.sitoolkit.wt.domain.evidence.selenium;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

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
import io.sitoolkit.wt.domain.evidence.ScreenshotTiming;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.TestException;
import io.sitoolkit.wt.infra.selenium.WebDriverUtils;
import lombok.Value;

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

    private static final String WINDOW_SIZE_GET_SCRIPT;
    static {
        StringBuilder sb = new StringBuilder();

        // In the Edge browser, since size values such as scrollHeight may be invalid
        // immediately after loading the page, refer to scrollHeight before return.
        // See also https://stackoverflow.com/a/3485654/10162817
        sb.append("document.body.scrollHeight;");

        sb.append("return {");
        sb.append("pageHeight: document.body.scrollHeight,");
        sb.append("pageWidth: document.body.scrollWidth,");
        sb.append("windowHeight: document.documentElement.clientHeight,");
        sb.append("windowWidth: document.documentElement.clientWidth,");
        sb.append("};");

        WINDOW_SIZE_GET_SCRIPT = sb.toString();
    }

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
    protected byte[] getAsData(ScreenshotTiming timing) {

        if (ScreenshotTiming.ON_DIALOG.equals(timing)) {
            return getDialogScreenshot();
        }

        if (Arrays.asList("chrome", "firefox", "edge").contains(pm.getDriverType())) {
            return getScreenshotWithAdjust();
        } else {
            return getScreenshot();
        }
    }

    protected byte[] getScreenshot() {

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

        return Base64.getDecoder().decode(data);
    }

    protected byte[] getDialogScreenshot() {
        if (robot == null) {
            return null;
        }

        Rectangle windowRect = current.getWindowRect();
        if (windowRect == null || windowRect.isEmpty()) {
            return null;
        }

        new WebDriverWait(driver, pm.getDialogWaitInSecond())
                .until(ExpectedConditions.alertIsPresent());

        BufferedImage img = robot.createScreenCapture(windowRect);

        current.setWindowRect(null);

        return image2byteArray(img);
    }

    private byte[] getScreenshotWithAdjust() {
        WindowSize windowSize = getWindowSize();

        BufferedImage img = new BufferedImage(windowSize.getPageWidth(), windowSize.getPageHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = img.getGraphics();

        if ((windowSize.getWindowHeight() >= windowSize.getPageHeight())
                && (windowSize.getWindowWidth() >= windowSize.getPageWidth())) {

            BufferedImage imageParts = getSizedScreenshot(windowSize.getWindowWidth(),
                    windowSize.getWindowHeight());

            graphics.drawImage(imageParts, 0, 0, null);

        } else {
            drawWholePageScreenshot(windowSize, graphics);
        }

        return image2byteArray(img);
    }

    private void drawWholePageScreenshot(WindowSize windowSize, Graphics graphics) {

        for (int scrollPosY = 0; scrollPosY < windowSize.getPageHeight(); scrollPosY += windowSize
                .getWindowHeight()) {

            int drawPosY = calcDrawPos(windowSize.getWindowHeight(), windowSize.getPageHeight(),
                    scrollPosY);

            for (int scrollPosX = 0; scrollPosX < windowSize
                    .getPageWidth(); scrollPosX += windowSize.getWindowWidth()) {

                int drawPosX = calcDrawPos(windowSize.getWindowWidth(), windowSize.getPageWidth(),
                        scrollPosX);

                scrollTo(scrollPosX, scrollPosY);

                BufferedImage imageParts = getSizedScreenshot(windowSize.getWindowWidth(),
                        windowSize.getWindowHeight());

                graphics.drawImage(imageParts, drawPosX, drawPosY, null);
            }
        }
    }

    private int calcDrawPos(int windowLength, int pageLength, int scrollPos) {
        if (windowLength >= pageLength) {
            return 0;
        }

        if (scrollPos + windowLength >= pageLength) {
            return pageLength - windowLength;
        } else {
            return scrollPos;
        }
    }

    private BufferedImage getSizedScreenshot(int windowWidth, int windowHeight) {
        try {
            BufferedImage imageParts = ImageIO
                    .read(takesScreenshot.getScreenshotAs(OutputType.FILE));
            return changeImageSize(imageParts, windowWidth, windowHeight);
        } catch (Exception e) {
            throw new TestException(e);
        }
    }

    private BufferedImage changeImageSize(BufferedImage imageParts, int windowWidth,
            int windowHeight) {

        int width = imageParts.getWidth();
        int height = imageParts.getHeight();

        int newHeight = windowWidth * height / width;

        AffineTransformOp xform = new AffineTransformOp(AffineTransform
                .getScaleInstance((double) windowWidth / width, (double) newHeight / height),
                AffineTransformOp.TYPE_BILINEAR);

        BufferedImage sizeChangeImg = new BufferedImage(windowWidth, newHeight,
                imageParts.getType());

        xform.filter(imageParts, sizeChangeImg);
        return sizeChangeImg;
    }

    private WindowSize getWindowSize() {

        @SuppressWarnings("unchecked")
        Map<String, Long> sizeMap = (Map<String, Long>) WebDriverUtils.executeScript(driver,
                WINDOW_SIZE_GET_SCRIPT);

        return new WindowSize(sizeMap.get("pageHeight").intValue(),
                sizeMap.get("pageWidth").intValue(), sizeMap.get("windowHeight").intValue(),
                sizeMap.get("windowWidth").intValue());
    }

    private void scrollTo(int x, int y) {
        JavascriptExecutor executor = ((JavascriptExecutor) driver);

        String driverType = StringUtils.defaultString(pm.getDriverType());
        if ("edge".equals(driverType)) {
            executor.executeAsyncScript("window.scrollTo(" + x + ", " + y + ");"
                    + "window.requestAnimationFrame(() => { arguments[arguments.length - 1](); });");
        } else {
            executor.executeScript("window.scrollTo(" + x + ", " + y + ");");
        }
    }

    private byte[] image2byteArray(BufferedImage img) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);

            return baos.toByteArray();
        } catch (IOException e) {
            throw new TestException(e);
        }
    }

    public void setResizeWindow(boolean resizeWindow) {
        this.resizeWindow = resizeWindow;
    }

    @Value
    private class WindowSize {
        private int pageHeight;
        private int pageWidth;
        private int windowHeight;
        private int windowWidth;
    }
}
