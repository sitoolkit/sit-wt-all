package org.sitoolkit.wt.domain.pageload.selenium;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageLoader;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;

public class InputTagLoader extends SeleniumPageLoader implements PageLoader {

    @Override
    public void load(PageContext ctx) {
        for (WebElement element : driver.findElements(By.tagName("input"))) {

            String type = element.getAttribute("type");
            if ("hidden".equals(type)) {
                continue;
            }

            String name = element.getAttribute("name");
            if (ctx.containsName(name)) {
                continue;
            }

            TestStep step = ctx.registTestStep(convert(element.getLocation()));


            if ("radio".equals(type) || "checkbox".equals(type)) {
                step.setOperationName("click");
            } else if ("submit".equals(type) || "button".equals(type)) {
                step.setOperationName("click");
                step.setScreenshotTiming("前");
            } else {
                step.setOperationName("input");
            }

            String id = element.getAttribute("id");

            if (StringUtils.isNotEmpty(id)) {
                step.setLocator(Locator.build("id", id));

                WebElement label = findLabelByForId(id);
                if (label != null) {
                    step.setItemName(label.getText());
                }

            } else {
                step.setLocator(Locator.build("name", name));
            }

        }
    }

}
