package io.sitoolkit.wt.domain.evidence.selenium;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.context.ApplicationContext;

import io.sitoolkit.wt.domain.evidence.Screenshot;
import io.sitoolkit.wt.domain.evidence.ScreenshotTaker;
import io.sitoolkit.wt.domain.evidence.ScreenshotTiming;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.selenium.WebDriverUtils;
import lombok.Value;

public class SeleniumScreenshotTaker extends ScreenshotTaker {

    @Resource
    ApplicationContext appCtx;

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

    private String windowSizeMapScript;

    @PostConstruct
    public void init() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            log.warn("warn", e);
        }

        waitTimeout = pm.getImplicitlyWait() / 1000;

        Map<String, String> sizeMap = new HashMap<>();
        sizeMap.put("pageHeight", "document.body.scrollHeight");
        sizeMap.put("pageWidth", "document.body.scrollWidth");
        sizeMap.put("windowHeight", "document.documentElement.clientHeight");
        sizeMap.put("windowWidth", "document.documentElement.clientWidth");
        windowSizeMapScript = sizeMap.entrySet().stream()
                .map((e) -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(",", "{", "}"));
    }

    @Override
    public Screenshot get(ScreenshotTiming timing) {

        if (ScreenshotTiming.ON_DIALOG.equals(timing)) {
            return getScreenShot(timing);
        }

        if (Arrays.asList("chrome", "firefox", "edge").contains(pm.getDriverType())) {
            return getScreenshotWithAdjust(timing);
        } else {
            return getScreenShot(timing);
        }
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

    private Screenshot getScreenshotWithAdjust(ScreenshotTiming timing) {
        Screenshot screenshot = appCtx.getBean(Screenshot.class);

        try {
            File file = File.createTempFile("sit-wt-temp-screenshot", ".png");

            WindowSize windowSize = getWindowSize();

            BufferedImage img = new BufferedImage(windowSize.getPageWidth(),
                    windowSize.getPageHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = img.getGraphics();

            if ((windowSize.getWindowHeight() >= windowSize.getPageHeight())
                    && (windowSize.getWindowWidth() >= windowSize.getPageWidth())) {

                BufferedImage imageParts = ImageIO.read(getAsFile());

                imageParts = imageSizeChange(imageParts, windowSize.getWindowWidth(),
                        windowSize.getWindowHeight());

                graphics.drawImage(imageParts, 0, 0, null);

            } else {
                drawWholePageScreenshot(windowSize, graphics);
            }

            ImageIO.write(img, "png", file);

            screenshot.setFile(file);
            screenshot.setTiming(timing);

        } catch (Exception e) {
            log.warn("screenshot.get.error", e);
            screenshot.clearElementPosition();
            screenshot.setErrorMesage(e.getLocalizedMessage());
        }

        return screenshot;

    }

    private BufferedImage imageSizeChange(BufferedImage imageParts, int windowWidth,
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

    private void drawWholePageScreenshot(WindowSize windowSize, Graphics graphics)
            throws IOException {

        for (int scrollPosY = 0; scrollPosY < windowSize.getPageHeight(); scrollPosY += windowSize
                .getWindowHeight()) {

            int drawPosY = calcDrawPos(windowSize.getWindowHeight(), windowSize.getPageHeight(),
                    scrollPosY);

            for (int scrollPosX = 0; scrollPosX < windowSize
                    .getPageWidth(); scrollPosX += windowSize.getWindowWidth()) {

                int drawPosX = calcDrawPos(windowSize.getWindowWidth(), windowSize.getPageWidth(),
                        scrollPosX);

                scrollTo(scrollPosX, scrollPosY);

                BufferedImage imageParts = ImageIO.read(getAsFile());

                imageParts = imageSizeChange(imageParts, windowSize.getWindowWidth(),
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

    private WindowSize getWindowSize() {

        // In the Edge browser, since size values such as scrollHeight may be invalid
        // immediately after loading the page, refer to scrollHeight before return.
        // See also https://stackoverflow.com/a/3485654/10162817
        @SuppressWarnings("unchecked")
        Map<String, Long> sizeMap = (Map<String, Long>) WebDriverUtils.executeScript(driver,
                "document.body.scrollHeight; return " + windowSizeMapScript + ";");

        return new WindowSize(sizeMap.get("pageHeight").intValue(),
                sizeMap.get("pageWidth").intValue(), sizeMap.get("windowHeight").intValue(),
                sizeMap.get("windowWidth").intValue());
    }

    private File getAsFile() {
        return takesScreenshot.getScreenshotAs(OutputType.FILE);
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
