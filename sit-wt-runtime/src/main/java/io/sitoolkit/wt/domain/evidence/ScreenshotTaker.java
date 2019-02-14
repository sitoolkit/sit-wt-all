package io.sitoolkit.wt.domain.evidence;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriverException;
import org.springframework.context.ApplicationContext;

import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import lombok.Value;

public abstract class ScreenshotTaker {

    protected final SitLogger log = SitLoggerFactory.getLogger(ScreenshotTaker.class);

    @Resource
    ApplicationContext appCtx;

    @Resource
    PropertyManager pm;

    public Screenshot get(ScreenshotTiming timing) {

        String driverType = StringUtils.defaultString(pm.getDriverType());

        if (("chrome".equals(driverType) || "firefox".equals(driverType)
                || "edge".equals(driverType)) && !ScreenshotTiming.ON_DIALOG.equals(timing)) {
            return getScreenshotWithAdjust(timing);
        } else {
            return getScreenShot(timing);
        }
    }

    private Screenshot getScreenShot(ScreenshotTiming timing) {
        Screenshot screenshot = appCtx.getBean(Screenshot.class);

        try {

            File file = File.createTempFile("sit-wt-temp-screenshot", ".png");
            String dataStr = ScreenshotTiming.ON_DIALOG.equals(timing) ? getDialogAsData()
                    : getAsData();
            byte[] data = Base64.getDecoder().decode(dataStr);
            FileUtils.writeByteArrayToFile(file, data);

            screenshot.setFile(file);
            screenshot.setTiming(timing);

        } catch (Exception e) {
            log.warn("screenshot.get.error", e);
            screenshot.clearElementPosition();
            screenshot.setErrorMesage(e.getLocalizedMessage());
        }

        return screenshot;
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
            throws WebDriverException, IOException {
        int scrollHeight = 0;
        int rowCount = 0;

        while (scrollHeight < windowSize.getPageHeight()) {
            int scrollPosY = windowSize.getWindowHeight() * rowCount;
            int drawPosY = 0;

            if (windowSize.getWindowHeight() < windowSize.getPageHeight()) {
                drawPosY = (scrollHeight + windowSize.getWindowHeight() >= windowSize
                        .getPageHeight())
                                ? windowSize.getPageHeight() - windowSize.getWindowHeight()
                                : scrollPosY;
            }

            int scrollWidth = 0;
            int colCount = 0;

            while (scrollWidth < windowSize.getPageWidth()) {
                int scrollPosX = windowSize.getWindowWidth() * colCount;
                int drawPosX = 0;

                if (windowSize.getWindowWidth() < windowSize.getPageWidth()) {
                    drawPosX = (scrollWidth + windowSize.getWindowWidth() >= windowSize
                            .getPageWidth())
                                    ? windowSize.getPageWidth() - windowSize.getWindowWidth()
                                    : scrollPosX;
                }

                scrollTo(scrollPosX, scrollPosY);

                BufferedImage imageParts = ImageIO.read(getAsFile());

                imageParts = imageSizeChange(imageParts, windowSize.getWindowWidth(),
                        windowSize.getWindowHeight());

                graphics.drawImage(imageParts, drawPosX, drawPosY, null);

                scrollWidth += windowSize.getWindowWidth();
                colCount++;
            }

            scrollHeight += windowSize.getWindowHeight();
            rowCount++;
        }
    }

    protected abstract String getAsData();

    protected abstract String getDialogAsData();

    protected abstract File getAsFile();

    protected abstract void scrollTo(int x, int y);

    protected abstract WindowSize getWindowSize();

    @Value
    protected class WindowSize {
        private int pageHeight;
        private int pageWidth;
        private int windowHeight;
        private int windowWidth;
    }
}
