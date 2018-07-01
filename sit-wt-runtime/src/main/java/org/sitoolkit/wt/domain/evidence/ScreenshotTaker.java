package org.sitoolkit.wt.domain.evidence;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.springframework.context.ApplicationContext;

public abstract class ScreenshotTaker {

    protected final SitLogger log = SitLoggerFactory.getLogger(ScreenshotTaker.class);

    @Resource
    ApplicationContext appCtx;

    @Resource
    TakesScreenshot takesScreenshot;

    @Resource
    PropertyManager pm;

    @Resource
    WebDriver driver;

    public Screenshot get(ScreenshotTiming timing) {

        String driverType = StringUtils.defaultString(pm.getDriverType());

        if ("chrome".equals(driverType) && !ScreenshotTiming.ON_DIALOG.equals(timing)) {
            return getChromeScreenshot(timing);
        } else {
            return getFireFoxScreenShot(timing);
        }
    }

    private Screenshot getFireFoxScreenShot(ScreenshotTiming timing) {
        Screenshot screenshot = appCtx.getBean(Screenshot.class);

        try {

            File file = File.createTempFile("sit-wt-temp-screenshot", ".png");
            String dataStr = ScreenshotTiming.ON_DIALOG.equals(timing) ? getDialogAsData()
                    : getAsData();
            byte[] data = Base64.decodeBase64(dataStr);
            FileUtils.writeByteArrayToFile(file, data);

            screenshot.setFile(file);
            screenshot.setTiming(timing);

        } catch (Exception e) {
            log.error("screenshot.get.error", e);
            screenshot.clearElementPosition();
            screenshot.setErrorMesage(e.getLocalizedMessage());
        }

        return screenshot;
    }

    private Screenshot getChromeScreenshot(ScreenshotTiming timing) {
        Screenshot screenshot = appCtx.getBean(Screenshot.class);

        try {
            File file = File.createTempFile("sit-wt-temp-screenshot", ".png");

            JavascriptExecutor executor = (JavascriptExecutor) driver;

            int pageHeight = Integer.parseInt(
                    String.valueOf(executor.executeScript("return document.body.scrollHeight")));
            int pageWidth = Integer.parseInt(
                    String.valueOf(executor.executeScript("return document.body.scrollWidth")));

            int windowHeight = Integer.parseInt(String.valueOf(
                    executor.executeScript("return document.documentElement.clientHeight")));
            int windowWidth = Integer.parseInt(String.valueOf(
                    executor.executeScript("return document.documentElement.clientWidth")));

            BufferedImage img = new BufferedImage(pageWidth, pageHeight,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = img.getGraphics();

            if ((windowHeight >= pageHeight) && (windowWidth >= pageWidth)) {
                BufferedImage imageParts = ImageIO
                        .read(takesScreenshot.getScreenshotAs(OutputType.FILE));

                imageParts = imageSizeChange(imageParts, windowWidth, windowHeight);

                graphics.drawImage(imageParts, 0, 0, null);

            } else {
                int scrollHeight = 0;
                int rowCount = 0;

                while (scrollHeight < pageHeight) {
                    int scrollPosY = windowHeight * rowCount;
                    int drawPosY = 0;

                    if (windowHeight < pageHeight) {
                        drawPosY = (scrollHeight + windowHeight >= pageHeight)
                                ? pageHeight - windowHeight : scrollPosY;
                    }

                    int scrollWidth = 0;
                    int colCount = 0;

                    while (scrollWidth < pageWidth) {
                        int scrollPosX = windowWidth * colCount;
                        int drawPosX = 0;

                        if (windowWidth < pageWidth) {
                            drawPosX = (scrollWidth + windowWidth >= pageWidth)
                                    ? pageWidth - windowWidth : scrollPosX;
                        }
                        executor.executeScript(
                                "window.scrollTo(" + scrollPosX + ", " + scrollPosY + ");");
                        BufferedImage imageParts = ImageIO
                                .read(takesScreenshot.getScreenshotAs(OutputType.FILE));

                        imageParts = imageSizeChange(imageParts, windowWidth, windowHeight);

                        graphics.drawImage(imageParts, drawPosX, drawPosY, null);

                        scrollWidth += windowWidth;
                        colCount++;
                    }

                    scrollHeight += windowHeight;
                    rowCount++;
                }
            }

            ImageIO.write(img, "png", file);

            screenshot.setFile(file);
            screenshot.setTiming(timing);

        } catch (IOException e) {
            log.error("スクリーンショットの取得に失敗しました {}", e);
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

    protected abstract String getAsData();

    protected abstract String getDialogAsData();
}
