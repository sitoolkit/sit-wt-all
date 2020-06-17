package io.sitoolkit.wt.domain.operation.selenium;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import io.sitoolkit.wt.domain.evidence.LogRecord;
import io.sitoolkit.wt.domain.evidence.MessagePattern;
import io.sitoolkit.wt.domain.operation.OperationResult;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.resource.MessageManager;

@Component
public class CommentOperation extends SeleniumOperation {

  @Override
  public void execute(TestStep testStep, SeleniumOperationContext ctx) {
    if (testStep.getLocator().isEmpty()) {
      ctx.info("msg", testStep.getValue());
    } else {
      WebElement element = findElement(testStep.getLocator());
      ctx.info(element, "msg", testStep.getValue());
    }
  }
}
