package org.sitoolkit.wt.domain.pageload.selenium;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageListener;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.SitPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumPageLietener implements PageListener {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumPageLietener.class);

    @Resource
    WebDriver driver;

    @Resource
    PropertyManager pm;

    @Override
    public void setUpPage(PageContext ctx) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
        ctx.setTitle(driver.getTitle());
        ctx.setUrl(driver.getCurrentUrl());
    }

    @Override
    public void tearDownPage(PageContext ctx) {
        driver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void setUp() {
        // touch WebDriver instance to start Browser
        String driverType = driver.toString();
        LOG.info("ブラウザを起動します {}", driverType);

        String baseUrl = pm.getBaseUrl();
        if (StringUtils.isNotEmpty(baseUrl)) {
            driver.get(SitPathUtils.buildUrl(baseUrl, ""));
        }
    }

    @Override
    public void tearDown() {
        // driver.quit();
    }

}
