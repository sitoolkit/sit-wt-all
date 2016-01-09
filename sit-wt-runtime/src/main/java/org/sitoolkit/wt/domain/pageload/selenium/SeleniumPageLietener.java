package org.sitoolkit.wt.domain.pageload.selenium;

import javax.annotation.Resource;

import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageListener;

public class SeleniumPageLietener implements PageListener {

    @Resource
    WebDriver driver;

    @Override
    public void setUpPage(PageContext ctx) {
        ctx.setTitle(driver.getTitle());
        ctx.setUrl(driver.getCurrentUrl());
    }

    @Override
    public void tearDownPage(PageContext ctx) {
        // NOP
    }

    @Override
    public void setUp() {
        // NOP
    }

    @Override
    public void tearDown() {
        driver.quit();
    }

}
