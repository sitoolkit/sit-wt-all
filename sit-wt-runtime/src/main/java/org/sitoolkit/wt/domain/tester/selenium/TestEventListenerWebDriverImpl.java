package org.sitoolkit.wt.domain.tester.selenium;

import javax.annotation.Resource;

import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.tester.TestEventListener;
import org.sitoolkit.wt.infra.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.SimpleThreadScope;

public class TestEventListenerWebDriverImpl implements TestEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestEventListenerWebDriverImpl.class);

    @Resource
    WebDriver driver;

    @Resource
    PropertyManager pm;

    @Resource
    SimpleThreadScope threadScope;

    @Override
    public void before() {
        // NOP
    }

    @Override
    public void after() {

        // EdgeDriver#deleteAllCookies doesn't work.
        // https://developer.microsoft.com/microsoft-edge/platform/issues/5751773/
        if (pm.isEdgeDriver()) {
            LOG.debug("WebDriverを再作成します {}", driver);
            threadScope.remove("scopedTarget.innerWebDriver");
        } else {
            LOG.debug("Coolkieを削除します {}", driver);
            driver.manage().deleteAllCookies();
        }

    }

}
