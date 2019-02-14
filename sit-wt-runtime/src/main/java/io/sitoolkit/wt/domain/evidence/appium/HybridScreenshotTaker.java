package io.sitoolkit.wt.domain.evidence.appium;

import java.io.File;

import javax.annotation.Resource;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import io.appium.java_client.AppiumDriver;
import io.sitoolkit.wt.domain.evidence.ScreenshotTaker;
import io.sitoolkit.wt.infra.selenium.WebDriverUtils;

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
    }

}
