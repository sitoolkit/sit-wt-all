package io.sitoolkit.wt.domain.pageload.selenium;

import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.sitoolkit.wt.domain.evidence.ElementPosition;
import io.sitoolkit.wt.domain.pageload.ElementId;
import io.sitoolkit.wt.domain.pageload.PageContext;
import io.sitoolkit.wt.domain.pageload.PageLoader;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestStep;

public abstract class SeleniumPageLoader implements PageLoader {

  @Resource
  protected WebDriver driver;

  protected ElementPosition convert(Point point) {
    return new ElementPosition(point.x, point.y, 0, 0);
  }

  protected WebElement findLabelByForId(String forId) {
    try {
      return driver.findElement(By.cssSelector("label[for='" + forId + "']"));
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  @Override
  public void load(PageContext ctx) {
    int formNum = 1;
    for (WebElement form : driver.findElements(By.tagName("form"))) {

      ElementId formId = new ElementId(formNum, form.getAttribute("id"));

      for (WebElement element : form.findElements(by())) {

        String name = element.getAttribute("name");
        if (ctx.containsNameInForm(formId, name)) {
          return;
        }

        loadForm(ctx, formId, element);
      }
    }
  }

  protected void loadForm(PageContext ctx, ElementId formId, WebElement element) {

  }

  protected By by() {
    return By.tagName("doesn't exists");
  }

  protected void setLocatorAndItemName(PageContext ctx, ElementId formId, WebElement element,
      TestStep step, String tagName) {
    String id = element.getAttribute("id");

    if (StringUtils.isNotEmpty(id)) {
      step.setLocator(Locator.build("id", id));

      WebElement label = findLabelByForId(id);
      if (label != null) {
        step.setItemName(label.getText());
      }

    } else {
      String name = element.getAttribute("name");

      // same name attribute element exists in same page
      if (ctx.containsName(name)) {

        String cssBase = " " + tagName + "[name='" + name + "']";
        if (StringUtils.isEmpty(formId.getId())) {
          step.setLocator(
              Locator.build("css", "form:nth-child(" + formId.getNth() + ")" + cssBase));
        } else {
          step.setLocator(Locator.build("css", "form#" + formId.getId() + cssBase));
        }

      } else {
        step.setLocator(Locator.build("name", name));
      }
    }

  }
}
