package io.sitoolkit.wt.domain.pageload.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import io.sitoolkit.wt.domain.pageload.PageContext;
import io.sitoolkit.wt.domain.pageload.PageLoader;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestStep;

public class AnchorTagLoader extends SeleniumPageLoader implements PageLoader {

  @Override
  public void load(PageContext ctx) {
    for (WebElement element : driver.findElements(By.tagName("a"))) {
      TestStep step = ctx.registTestStep(convert(element.getLocation()), null);

      String text = element.getText();
      step.setLocator(Locator.build("link", text));
      step.setItemName(text);
      step.setOperationName("click");
      step.setScreenshotTiming("前");

    }
  }

}
