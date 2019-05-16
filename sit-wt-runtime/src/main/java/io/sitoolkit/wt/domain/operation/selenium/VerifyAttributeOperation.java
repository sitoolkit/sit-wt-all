package io.sitoolkit.wt.domain.operation.selenium;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import io.sitoolkit.wt.domain.testscript.TestStep;

@Component
public class VerifyAttributeOperation extends VerifyOperation {

  @Override
  protected String getActual(WebElement element, TestStep testStep) {
    return StringUtils.defaultString(element.getAttribute(testStep.getLocator().getAttributeName()),
        "null");
  }
}
