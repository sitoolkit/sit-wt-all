package io.sitoolkit.wt.domain.operation.selenium;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestStep;

@Component
public class CommentOperation extends SeleniumOperation {

  @Override
  public void execute(TestStep testStep, SeleniumOperationContext ctx) {
    String comment = testStep.getValue();
    Locator locator = testStep.getLocator();
    if (locator.getTypeVo() == Locator.Type.na) {
      ctx.info("comment", comment);
    } else {
      WebElement element = findElement(locator);
      ctx.info(element, "comment", comment);
    }
  }
}
