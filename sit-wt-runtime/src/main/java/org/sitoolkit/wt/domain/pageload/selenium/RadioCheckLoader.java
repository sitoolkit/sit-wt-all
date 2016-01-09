package org.sitoolkit.wt.domain.pageload.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageLoader;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;

public class RadioCheckLoader extends SeleniumPageLoader implements PageLoader {


    @Override
    public void load(PageContext ctx) {
        for (WebElement element : driver.findElements(
                By.cssSelector("input[type='radio'],input[type='checkbox']"))) {

            String name = element.getAttribute("name");
            if (ctx.containsName(name)) {
                continue;
            }

            int caseNo = 1;
            TestStep step = null;
            for (WebElement choice : driver.findElements(By.name(name))) {
                WebElement choiceLabel = findLabelByForId(choice.getAttribute("id"));

                if (choiceLabel == null) {
                    break;
                }

                if (step == null) {
                    step = ctx.create();
                    step.setOperationName("choose");
                    step.setLocator(Locator.build("name", name));
                    step.setDataType("label");
                    ctx.add(convert(element.getLocation()), step);
                }

                step.setTestData("00" + Integer.toString(caseNo++), choiceLabel.getText());
            }
        }

    }

}
