package org.sitoolkit.wt.domain.debug.selenium;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.debug.LocatorChecker;
import org.sitoolkit.wt.domain.operation.selenium.SeleniumOperation;
import org.sitoolkit.wt.domain.operation.selenium.SeleniumOperationContext;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 * {@link LocatorChecker}のSelenium実装です。
 *
 * @author yuichi.kuwahara
 *
 */
@Component
public class SeleniumLocatorChecker extends SeleniumOperation implements LocatorChecker {

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        // NOP
    }

    @Override
    public void check(Locator locator) {
        try {
            List<WebElement> elements = seleniumDriver.findElements(by(locator));

            if (elements.isEmpty()) {
                log.info("ロケーター({})に該当する要素は見つかりませんでした。", locator);
                return;
            }
            StringBuilder sb = new StringBuilder();

            for (WebElement element : elements) {
                sb.append("\n    ");
                sb.append("<");
                sb.append(element.getTagName());

                sb.append(getAttribute(element, "id"));
                sb.append(getAttribute(element, "type"));
                sb.append(getAttribute(element, "name"));
                sb.append(getAttribute(element, "class"));
                sb.append(getAttribute(element, "href"));
                sb.append(getAttribute(element, "src"));
                sb.append(getAttribute(element, "value"));

                sb.append("...");
            }

            log.info("ロケーター({})に該当する要素が見つかりました。{}", locator, sb);
        } catch (InvalidSelectorException e) {
            log.info("ロケーター({})が不正です。{}", locator, e.getLocalizedMessage());
        }
    }

    private String getAttribute(WebElement element, String attr) {
        String attrVal = element.getAttribute(attr);
        if (StringUtils.isEmpty(attrVal)) {
            return "";
        }
        return " " + attr + "=\"" + attrVal + "\"";

    }

}
