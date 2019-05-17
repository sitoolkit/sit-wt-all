package io.sitoolkit.wt.domain.webdriver;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.context.annotation.Profile;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.firefox.FirefoxManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.selenium.WebDriverCloser;
import io.sitoolkit.wt.infra.selenium.WebDriverInstaller;

@Profile("mobile")
public class MobileWebDriver {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(MobileWebDriver.class);

  public RemoteWebDriver getMobileDriver(PropertyManager pm, WebDriverCloser closer,
      WebDriverInstaller webDriverInstaller, FirefoxManager firefoxManager)
      throws MalformedURLException {
    RemoteWebDriver webDriver = null;

    String driverType = StringUtils.defaultString(pm.getDriverType());
    DesiredCapabilities capabilities = new DesiredCapabilities();

    Map<String, String> map = pm.getCapabilities();

    for (Entry<String, String> entry : map.entrySet()) {
      capabilities.setCapability(entry.getKey(), entry.getValue());
    }

    LOG.info("webdriver.start", driverType, capabilities);

    switch (driverType) {
      case "android":
        LOG.info("webdriver.android", pm.getAppiumAddress());
        webDriver = new AndroidDriver<>(pm.getAppiumAddress(), capabilities);
        break;

      case "ios":
        LOG.info("webdriver.ios", pm.getAppiumAddress());
        webDriver = new IOSDriver<>(pm.getAppiumAddress(), capabilities);
        break;

      default: // include firefox
        // geckodriver is not stable yet as of 2016/10
        // so we doesn't support neigther selenium 3 nor firefox 48.x
        // higher
        webDriverInstaller.installGeckoDriver();

        webDriver = firefoxManager.startWebDriver(capabilities);
    }

    webDriver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(), TimeUnit.MILLISECONDS);

    closer.register(webDriver);

    LOG.debug("webdriver.init", webDriver);

    return webDriver;
  }

}
