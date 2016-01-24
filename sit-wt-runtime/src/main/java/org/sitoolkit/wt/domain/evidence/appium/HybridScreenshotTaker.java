package org.sitoolkit.wt.domain.evidence.appium;

import javax.annotation.Resource;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.sitoolkit.wt.domain.evidence.ScreenshotTaker;

import io.appium.java_client.AppiumDriver;

public class HybridScreenshotTaker extends ScreenshotTaker {

    private static final String CONTEXT_NATIVE_APP = "NATIVE_APP";

    @Resource
    AppiumDriver<?> driver;

    @Resource
    TakesScreenshot takesScreenshot;

    @Override
    public String getAsData() {
        String context = driver.getContext();
        if (CONTEXT_NATIVE_APP.equals(context)) {
            context = null;
        } else {
            driver.context(CONTEXT_NATIVE_APP);
        }

        String data = takesScreenshot.getScreenshotAs(OutputType.BASE64);

        if (context != null) {
            // TODO 何故かdriver.getContextHandles()を実行してからcontextを設定しないと例外が発生する。
            driver.getContextHandles();
            driver.context(context);
        }

        return data;
    }

    @Override
    public String getDialogAsData() {
        return getAsData();
    }

}
