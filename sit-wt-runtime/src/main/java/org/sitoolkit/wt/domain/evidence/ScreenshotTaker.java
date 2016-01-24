package org.sitoolkit.wt.domain.evidence;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public abstract class ScreenshotTaker {

    protected final Logger log = LoggerFactory.getLogger(ScreenshotTaker.class);

    @Resource
    ApplicationContext appCtx;

    public Screenshot get(ScreenshotTiming timing) {

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
            log.error("スクリーンショットの取得に失敗しました {}", e.getLocalizedMessage());
            screenshot.clearElementPosition();
            screenshot.setErrorMesage(e.getLocalizedMessage());
        }

        return screenshot;
    }

    protected abstract String getAsData();

    protected abstract String getDialogAsData();
}
