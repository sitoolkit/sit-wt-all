package io.sitoolkit.wt.domain.evidence;

import java.io.File;
import java.util.Base64;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;

import io.sitoolkit.wt.infra.TestException;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public abstract class ScreenshotTaker {

    protected final SitLogger log = SitLoggerFactory.getLogger(ScreenshotTaker.class);

    @Resource
    ApplicationContext appCtx;

    public Screenshot get(ScreenshotTiming timing) {
        try {
            return getScreenshot(timing);
        } catch (Exception e) {
            log.warn("screenshot.get.error", e);
            Screenshot screenshot = appCtx.getBean(Screenshot.class);
            screenshot.clearElementPosition();
            screenshot.setErrorMesage(e.getLocalizedMessage());
            return screenshot;
        }
    }

    protected Screenshot createScreenshot(ScreenshotTiming timing, String dataStr) {
        Screenshot screenshot = appCtx.getBean(Screenshot.class);

        try {

            File file = File.createTempFile("sit-wt-temp-screenshot", ".png");
            byte[] data = Base64.getDecoder().decode(dataStr);
            FileUtils.writeByteArrayToFile(file, data);

            screenshot.setFile(file);
            screenshot.setTiming(timing);

        } catch (Exception e) {
            throw new TestException(e);
        }

        return screenshot;
    }

    protected abstract Screenshot getScreenshot(ScreenshotTiming timing);

}
