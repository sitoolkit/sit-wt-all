package org.sitoolkit.wt.domain.pageload.selenium;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageLoader;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;

public class SelectTagLoader extends SeleniumPageLoader implements PageLoader {

    @Override
    public void load(PageContext ctx) {
        for (WebElement element : driver.findElements(By.tagName("select"))) {

            TestStep step = ctx.registTestStep(convert(element.getLocation()));

            step.setOperationName("select");

            String id = element.getAttribute("id");
            if (StringUtils.isNotEmpty(id)) {
                step.setLocator(Locator.build("id", id));

                WebElement label = findLabelByForId(id);
                if (label != null) {
                    step.setItemName(label.getText());
                }

            } else {
                String name = element.getAttribute("name");
                step.setLocator(Locator.build("name", name));
            }


            int cnt = 1;
            for (WebElement option : element.findElements(By.tagName("option"))) {
                step.setTestData("00" + Integer.toString(cnt++), option.getText());
                step.setDataType("label");
            }
        }

    }

}
