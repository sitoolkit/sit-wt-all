package org.sitoolkit.wt.domain.operation.appium;

import org.sitoolkit.wt.domain.operation.selenium.OpenOperation;
import org.sitoolkit.wt.domain.operation.selenium.SeleniumOperationContext;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

import io.appium.java_client.AppiumDriver;

@Component
public class StartAppOperation extends OpenOperation {

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {

        if (seleniumDriver instanceof AppiumDriver<?>) {
            AppiumDriver<?> appiumDriver = (AppiumDriver<?>) seleniumDriver;
            for (String contextHandle : appiumDriver.getContextHandles()) {
                if (contextHandle.startsWith("WEBVIEW")) {
                    appiumDriver.context(contextHandle);
                }
            }
        } else {
            super.execute(testStep, ctx);
        }
    }
}
