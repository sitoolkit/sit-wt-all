package org.sitoolkit.wt.domain.tester.selenium;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverCloser {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverCloser.class);

    private List<WebDriver> driverList = new ArrayList<>();

    public void register(WebDriver driver) {
        driverList.add(driver);
    }

    @PreDestroy
    public void close() {
        driverList.parallelStream().forEach(driver -> {
            LOG.debug("WebDriverを停止します {}", driver);
            try {
                driver.quit();
            } catch (Exception e) {
                LOG.trace(e.getMessage());
            }
        });
    }
}
