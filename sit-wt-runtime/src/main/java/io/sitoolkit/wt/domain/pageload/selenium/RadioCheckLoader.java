package io.sitoolkit.wt.domain.pageload.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import io.sitoolkit.wt.domain.pageload.ElementId;
import io.sitoolkit.wt.domain.pageload.PageContext;
import io.sitoolkit.wt.domain.pageload.PageLoader;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestStep;

public class RadioCheckLoader extends SeleniumPageLoader implements PageLoader {

  @Override
  protected void loadForm(PageContext ctx, ElementId formId, WebElement element) {

    String name = element.getAttribute("name");
    if (ctx.containsName(name)) {
      return;
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
        ctx.add(convert(element.getLocation()), step, formId);
      }

      step.setTestData("00" + Integer.toString(caseNo++), choiceLabel.getText());
    }

  }

  @Override
  protected By by() {
    return By.cssSelector("input[type='radio'],input[type='checkbox']");
  }

}
