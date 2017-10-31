package org.sitoolkit.wt.domain.tester.selenium;

import javax.annotation.Resource;

import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.tester.TestEventListener;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.springframework.context.support.SimpleThreadScope;

public class TestEventListenerWebDriverImpl implements TestEventListener {

    private static final SitLogger LOG = SitLoggerFactory
            .getLogger(TestEventListenerWebDriverImpl.class);

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
            LOG.debug("webdriver.reconstruction", driver);
            threadScope.remove("scopedTarget.innerWebDriver");

        } else if (pm.isRemoteDriver()) {
            LOG.debug("webdriver.remote.end", driver);
            driver.quit();
            threadScope.remove("scopedTarget.innerWebDriver");

        } else {
            LOG.debug("cookie.delete", driver);
            driver.manage().deleteAllCookies();
        }

    }

}
