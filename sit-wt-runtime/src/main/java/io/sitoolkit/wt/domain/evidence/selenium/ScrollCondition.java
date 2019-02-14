package io.sitoolkit.wt.domain.evidence.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.selenium.WebDriverUtils;

public class ScrollCondition implements ExpectedCondition<Boolean> {

    protected final SitLogger LOG = SitLoggerFactory.getLogger(ScrollCondition.class);

    private int expectedX;
    private int expectedY;

    public ScrollCondition(int x, int y) {
        this.expectedX = x;
        this.expectedY = y;
    }

    public Boolean apply(WebDriver driver) {
        int scrollLeft = Integer.parseInt(String.valueOf(WebDriverUtils.executeScript(driver,
                "return document.querySelector('html').scrollLeft || document.body.scrollLeft")));
        int scrollTop = Integer.parseInt(String.valueOf(WebDriverUtils.executeScript(driver,
                "return document.querySelector('html').scrollTop || document.body.scrollTop")));

        LOG.infoMsg(scrollLeft + ", " + scrollTop + " && " + expectedX + ", " + expectedY);

        return scrollLeft == expectedX && scrollTop == expectedY;
    }
}
