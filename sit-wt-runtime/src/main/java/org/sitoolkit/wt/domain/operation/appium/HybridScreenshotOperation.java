package org.sitoolkit.wt.domain.operation.appium;

import java.io.File;

import javax.annotation.Resource;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.sitoolkit.wt.domain.operation.ScreenshotOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.appium.java_client.AppiumDriver;

public class HybridScreenshotOperation implements ScreenshotOperation {

    private static final String CONTEXT_NATIVE_APP = "NATIVE_APP";

    private static final Logger LOG = LoggerFactory.getLogger(HybridScreenshotOperation.class);

    @Resource
    AppiumDriver<?> driver;

    @Override
    public File get() {
        if (driver instanceof TakesScreenshot) {

            String context = driver.getContext();
            if (CONTEXT_NATIVE_APP.equals(context)) {
                context = null;
            } else {
                driver.context(CONTEXT_NATIVE_APP);
            }

            File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            if (context != null) {
                // 何故かdriver.getContextHandles()を実行してからcontextを設定しないと例外が発生する。
                driver.getContextHandles();
                driver.context(context);
            }

            return file;
        } else {
            LOG.warn("ドライバ{}はスクリーンショットを取得できません。", driver.getClass().getName());
            return null;
        }
    }

    @Override
    public File getWithDialog() {
        return get();
    }

}
