package org.sitoolkit.wt.domain.operation.appium;

import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.operation.selenium.SeleniumOperation;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

@Component
public class SpinOperation extends SeleniumOperation {

    @Override
    public void execute(TestStep testStep) {
        WebElement element = findElement(testStep.getLocator());
        element.click();

        switch (testStep.getDataType()) {
        default:
            findElement(Locator.build(testStep.getDataType(), testStep.getValue())).click();
            break;
        }
        info(testStep.getValue() + "の選択肢", "選択", element);
    }

}
