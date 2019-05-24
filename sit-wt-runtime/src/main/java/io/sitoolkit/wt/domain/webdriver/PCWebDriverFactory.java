package io.sitoolkit.wt.domain.webdriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.springframework.context.annotation.Profile;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.firefox.FirefoxManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.selenium.WebDriverCloser;
import io.sitoolkit.wt.infra.selenium.WebDriverInstaller;

@Profile("pc")
public class PCWebDriverFactory {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(PCWebDriverFactory.class);

  private static final String DEFAULT_DRIVER_TYPE = "chrome";

  private static final String SECOND_DEFAULT_DRIVER_TYPE = "firefox";

  private int windowShiftLeft = 0;

  private int windowShiftTop = 0;

  private String defaultDriverType = DEFAULT_DRIVER_TYPE;

  public RemoteWebDriver createPCDriver(PropertyManager pm, WebDriverCloser closer,
      FirefoxManager firefoxManager) throws MalformedURLException {

    String driverType = StringUtils.defaultString(pm.getDriverType());
    DesiredCapabilities capabilities = new DesiredCapabilities();

    Map<String, String> map = pm.getCapabilities();

    for (Entry<String, String> entry : map.entrySet()) {
      capabilities.setCapability(entry.getKey(), entry.getValue());
    }

    LOG.info("webdriver.start", driverType, capabilities);

    RemoteWebDriver webDriver = selectWebDriver(driverType, pm, firefoxManager, capabilities);

    webDriver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(), TimeUnit.MILLISECONDS);

    webDriver.manage().window().setPosition(
        new Point(pm.getWindowLeft() + windowShiftLeft, pm.getWindowTop() + windowShiftTop));
    windowShiftTop += pm.getWindowShiftTop();
    windowShiftLeft += pm.getWindowShiftLeft();

    Dimension windowSize = new Dimension(pm.getWindowWidth(), pm.getWindowHeight());
    webDriver.manage().window().setSize(windowSize);

    closer.register(webDriver);

    LOG.debug("webdriver.init", webDriver);

    return webDriver;
  }

  private RemoteWebDriver selectWebDriver(String driverType, PropertyManager pm,
      FirefoxManager firefoxManager, DesiredCapabilities capabilities)
      throws MalformedURLException {

    if (StringUtils.isNotEmpty(driverType)) {
      return selectWebDriver2(driverType, pm, firefoxManager, capabilities);
    }

    try {
      return selectWebDriver2(defaultDriverType, pm, firefoxManager, capabilities);
    } catch (WebDriverException e) {
      // NOP
    }

    defaultDriverType = SECOND_DEFAULT_DRIVER_TYPE;

    return selectWebDriver2(defaultDriverType, pm, firefoxManager, capabilities);
  }

  private RemoteWebDriver selectWebDriver2(String driverType, PropertyManager pm,
      FirefoxManager firefoxManager, DesiredCapabilities capabilities)
      throws MalformedURLException {

    switch (driverType) {

      case "chrome":
        WebDriverInstaller.getInstance().installChromeDriver();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("password_manager_enabled", false);
        Map<String, Object> options = new HashMap<>();
        options.put("prefs", prefs);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        ChromeOptions chromeOptions = new ChromeOptions().merge(capabilities);
        return new ChromeDriver(chromeOptions);

      case "ie":
      case "internet explorer":
        WebDriverInstaller.getInstance().installIeDriver();
        InternetExplorerOptions ieOptions = new InternetExplorerOptions(capabilities);
        return new InternetExplorerDriver(ieOptions);

      case "edge":
        WebDriverInstaller.getInstance().installEdgeDriver();
        EdgeOptions edgeOptions = new EdgeOptions().merge(capabilities);
        return new EdgeDriver(edgeOptions);

      case "safari":
        SafariOptions safariOptions = new SafariOptions(capabilities);
        try {
          return new SafariDriver(safariOptions);
        } catch (UnreachableBrowserException e) {
          if (StringUtils.startsWith(e.getMessage(), "Failed to connect to SafariDriver")) {
            WebDriverInstaller.getInstance().installSafariDriver();
            return new SafariDriver(safariOptions);
          }
        }
        break;

      case "remote":
        LOG.info("webdriver.remote", pm.getHubUrl());
        return new RemoteWebDriver(new URL(pm.getHubUrl()), capabilities);

      case "firefox":
      case "ff":
        WebDriverInstaller.getInstance().installGeckoDriver();
        return firefoxManager.startWebDriver(capabilities);

      default:
        return null;
    }

    return null;
  }

}
