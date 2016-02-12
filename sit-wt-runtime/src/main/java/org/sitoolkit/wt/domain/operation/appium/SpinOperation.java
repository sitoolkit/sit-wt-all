package org.sitoolkit.wt.domain.operation.appium;

import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.MessagePattern;
import org.sitoolkit.wt.domain.operation.selenium.SeleniumOperation;
import org.sitoolkit.wt.domain.operation.selenium.SeleniumOperationContext;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

@Component
public class SpinOperation extends SeleniumOperation {

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        WebElement element = findElement(testStep.getLocator());
        element.click();

        switch (testStep.getDataType()) {
        default:
            findElement(Locator.build(testStep.getDataType(), testStep.getValue())).click();
            break;
        }

        ctx.info(element, MessagePattern.項目にXXをYYします, testStep.getValue() + "の選択肢", "選択");
    }

}
