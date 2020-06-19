package io.sitoolkit.wt.domain.operation.selenium;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import io.sitoolkit.wt.domain.testscript.TestStep;

@Component
public class CommentOperation extends SeleniumOperation {

  @Override
  public void execute(TestStep testStep, SeleniumOperationContext ctx) {
    if (StringUtils.isEmpty(testStep.getLocator().getValue())) {
      ctx.info("comment", testStep.getValue());
    } else {
      WebElement element = findElement(testStep.getLocator());
      ctx.info(element, "comment", testStep.getValue());
    }
  }
}
