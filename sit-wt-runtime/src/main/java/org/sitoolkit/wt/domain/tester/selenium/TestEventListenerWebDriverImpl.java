package org.sitoolkit.wt.domain.tester.selenium;

import javax.annotation.Resource;

import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.tester.TestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEventListenerWebDriverImpl implements TestEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestEventListenerWebDriverImpl.class);

    @Resource
    WebDriver driver;

    @Override
    public void before() {
        // NOP
    }

    @Override
    public void after() {
        LOG.debug("Coolkieを削除します {}", driver);
        driver.manage().deleteAllCookies();
    }

}
