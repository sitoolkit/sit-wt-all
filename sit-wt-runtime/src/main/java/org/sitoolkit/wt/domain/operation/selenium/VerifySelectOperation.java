package org.sitoolkit.wt.domain.operation.selenium;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.VerifyException;
import org.springframework.stereotype.Component;

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
    public void execute(TestStep testStep) {
        String[] expectedValues = testStep.getValues();
        WebElement element = findElement(testStep.getLocator());
        Select select = new Select(element);

        info(element, "{}({})で選択された値が期待値{}に一致することを確認します。", new Object[] { testStep.getItemName(),
                testStep.getLocator(), Arrays.toString(expectedValues) });

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
            throw new VerifyException("{0}({1})で選択された値は期待値と異なります。{2}", testStep.getItemName(),
                    testStep.getLocator(), sb.toString());
        }

    }

    String toMessage(int index, WebElement option) {
        return index + "番目の選択肢[" + option.getText() + "](値= " + option.getAttribute("value") + ")";
    }

    interface OptionSupport {
        boolean expectsSelected(String[] expectedValues, int index, WebElement option);
    }
}
