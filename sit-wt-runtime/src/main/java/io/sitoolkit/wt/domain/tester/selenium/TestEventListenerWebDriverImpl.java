package io.sitoolkit.wt.domain.tester.selenium;

import java.net.ConnectException;
import javax.annotation.Resource;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.springframework.context.support.SimpleThreadScope;
import io.sitoolkit.wt.domain.tester.TestEventListener;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class TestEventListenerWebDriverImpl implements TestEventListener {

  private static final SitLogger LOG =
      SitLoggerFactory.getLogger(TestEventListenerWebDriverImpl.class);

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
      try {
        driver.manage().deleteAllCookies();
      } catch (NoSuchSessionException e) {
        // can be ignored
      } catch (WebDriverException e) {
        // ConnectException can be ignored
        if (!ConnectException.class.isInstance(e)) {
          throw e;
        }
      }
    }

  }

}
