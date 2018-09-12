package io.sitoolkit.wt.infra.selenium;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.openqa.selenium.WebDriver;

import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class WebDriverCloser {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(WebDriverCloser.class);

    private List<WebDriver> driverList = new ArrayList<>();

    @Resource
    PropertyManager pm;

    public void register(WebDriver driver) {
        driverList.add(driver);
    }

    /**
    *
    */
    @PreDestroy
    public void preDestroy() {
        if (pm.isRemoteDriver()) {
            return;
        }

        driverList.parallelStream().forEach(driver -> {
            LOG.debug("webdriver.stop", driver);
            try {
                driver.quit();
            } catch (Exception e) {
                LOG.trace("trace", e.getMessage());
            }
        });
    }

}
