package io.sitoolkit.wt.domain.pageload.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import io.sitoolkit.wt.domain.pageload.ElementId;
import io.sitoolkit.wt.domain.pageload.PageContext;
import io.sitoolkit.wt.domain.pageload.PageLoader;
import io.sitoolkit.wt.domain.testscript.TestStep;

public class SelectTagLoader extends SeleniumPageLoader implements PageLoader {

  @Override
  protected void loadForm(PageContext ctx, ElementId formId, WebElement element) {
    TestStep step = ctx.registTestStep(convert(element.getLocation()), formId);

    step.setOperationName("select");

    setLocatorAndItemName(ctx, formId, element, step, "select");

    int cnt = 1;
    for (WebElement option : element.findElements(By.tagName("option"))) {
      step.setTestData("00" + Integer.toString(cnt++), option.getText());
      step.setDataType("label");
    }

  }

  @Override
  protected By by() {
    return By.tagName("select");
  }
}
