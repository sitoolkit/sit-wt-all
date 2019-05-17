package io.sitoolkit.wt.domain.operation.selenium;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.VerifyException;
import io.sitoolkit.wt.infra.resource.MessageManager;

@Component
public class VerifySelectOperation extends SeleniumOperation {

  private static Map<String, OptionSupport> map = new HashMap<>();

  static {
    map.put("index", new OptionSupport() {
      @Override
      public boolean expectsSelected(String[] expectedValues, int index, WebElement option) {
        return ArrayUtils.contains(expectedValues, Integer.toString(index + 1));
      }
    });

    map.put("label", new OptionSupport() {

      @Override
      public boolean expectsSelected(String[] expectedValues, int index, WebElement option) {
        return ArrayUtils.contains(expectedValues, option.getText());
      }
    });

    map.put("value", new OptionSupport() {

      @Override
      public boolean expectsSelected(String[] expectedValues, int index, WebElement option) {
        return ArrayUtils.contains(expectedValues, option.getAttribute("value"));
      }
    });

    map.put("", map.get("value"));
  }

  @Override
  public void execute(TestStep testStep, SeleniumOperationContext ctx) {
    String[] expectedValues = testStep.getValues();
    WebElement element = findElement(testStep.getLocator());
    Select select = new Select(element);

    ctx.info(element, "verify.select", new Object[] {testStep.getItemName(), testStep.getLocator(),
        Arrays.toString(expectedValues)});

    OptionSupport support = map.get(testStep.getDataType());

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < select.getOptions().size(); i++) {
      WebElement option = select.getOptions().get(i);

      if (support.expectsSelected(expectedValues, i, option)) {
        if (!"true".equalsIgnoreCase(option.getAttribute("selected"))) {
          sb.append(toMessage(i, option));
          sb.append("は選択されていません。");
        }
      } else {
        if ("true".equalsIgnoreCase(option.getAttribute("selected"))) {
          sb.append(toMessage(i, option));
          sb.append("は選択されています。");
        }
      }
    }

    if (sb.length() > 0) {
      throw new VerifyException(MessageManager.getMessage("verify.select.unmatch"),
          testStep.getItemName(), testStep.getLocator(), sb.toString());
    }

  }

  String toMessage(int index, WebElement option) {
    return index + "番目の選択肢[" + option.getText() + "](値= " + option.getAttribute("value") + ")";
  }

  interface OptionSupport {
    boolean expectsSelected(String[] expectedValues, int index, WebElement option);
  }
}
