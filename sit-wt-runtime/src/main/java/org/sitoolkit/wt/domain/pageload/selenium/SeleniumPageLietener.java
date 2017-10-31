package org.sitoolkit.wt.domain.pageload.selenium;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.guidance.GuidanceUtils;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageListener;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;

public class SeleniumPageLietener implements PageListener {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(SeleniumPageLietener.class);

    @Resource
    WebDriver driver;

    @Resource
    PropertyManager pm;

    private String guidanceFile = "guidance/guidance-page2script.html";

    private String[] guidanceResources = new String[] { guidanceFile,
            "guidance/css/bootstrap.min.css", "guidance/css/style.css", "guidance/js/open.js",
            "guidance/img/ic_file_download_black_18dp_1x.png",
            "guidance/img/ic_stop_black_18dp_1x.png" };

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

        GuidanceUtils.retrieve(guidanceResources);

        // touch WebDriver instance to start Browser
        String driverType = driver.toString();
        LOG.info("ブラウザを起動します {}", driverType);

        String baseUrl = System.getProperty("baseUrl");
        driver.get(GuidanceUtils.appendBaseUrl(guidanceFile, baseUrl));
    }

    @Override
    public void tearDown() {
        // driver.quit();
    }

}
