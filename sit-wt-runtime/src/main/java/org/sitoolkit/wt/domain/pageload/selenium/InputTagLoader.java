package org.sitoolkit.wt.domain.pageload.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.pageload.ElementId;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageLoader;
import org.sitoolkit.wt.domain.testscript.TestStep;

public class InputTagLoader extends SeleniumPageLoader implements PageLoader {

    @Override
    protected void loadForm(PageContext ctx, ElementId formId, WebElement element) {
        String type = element.getAttribute("type");
        if ("hidden".equals(type)) {
            return;
        }

        TestStep step = ctx.registTestStep(convert(element.getLocation()), formId);

        if ("radio".equals(type) || "checkbox".equals(type)) {
            step.setOperationName("click");
        } else if ("submit".equals(type) || "button".equals(type)) {
            step.setOperationName("click");
            step.setScreenshotTiming("前");
            step.setItemName(element.getAttribute("value"));
        } else {
            step.setOperationName("input");
        }

        setLocatorAndItemName(ctx, formId, element, step, "input");

    }

    @Override
    protected By by() {
        return By.tagName("input");
    }

}
