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
public class PCWebDriver {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(PCWebDriver.class);

    private int windowShiftLeft = 0;

    private int windowShiftTop = 0;

    public RemoteWebDriver getPCDriver(PropertyManager pm, WebDriverCloser closer,
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

            case "chrome":
                webDriverInstaller.installChromeDriver();

                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("password_manager_enabled", false);
                Map<String, Object> options = new HashMap<>();
                options.put("prefs", prefs);
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);

                ChromeOptions chromeOptions = new ChromeOptions().merge(capabilities);
                webDriver = new ChromeDriver(chromeOptions);
                break;

            case "ie":
            case "internet explorer":
                webDriverInstaller.installIeDriver();
                InternetExplorerOptions ieOptions = new InternetExplorerOptions(capabilities);
                webDriver = new InternetExplorerDriver(ieOptions);
                break;

            case "edge":
                webDriverInstaller.installEdgeDriver();
                EdgeOptions edgeOptions = new EdgeOptions().merge(capabilities);
                webDriver = new EdgeDriver(edgeOptions);
                break;

            case "safari":
                SafariOptions safariOptions = new SafariOptions(capabilities);
                try {
                    webDriver = new SafariDriver(safariOptions);
                } catch (UnreachableBrowserException e) {
                    if (StringUtils.startsWith(e.getMessage(),
                            "Failed to connect to SafariDriver")) {
                        webDriverInstaller.installSafariDriver();
                        webDriver = new SafariDriver(safariOptions);
                    }
                }
                break;

            case "remote":
                LOG.info("webdriver.remote", pm.getHubUrl());
                webDriver = new RemoteWebDriver(new URL(pm.getHubUrl()), capabilities);

                break;

            default: // include firefox
                // geckodriver is not stable yet as of 2016/10
                // so we doesn't support neigther selenium 3 nor firefox 48.x
                // higher
                webDriverInstaller.installGeckoDriver();

                webDriver = firefoxManager.startWebDriver(capabilities);
        }

        webDriver.manage().timeouts().implicitlyWait(pm.getImplicitlyWait(), TimeUnit.MILLISECONDS);

        webDriver.manage().window().setPosition(new Point(pm.getWindowLeft() + windowShiftLeft,
                pm.getWindowTop() + windowShiftTop));
        windowShiftTop += pm.getWindowShiftTop();
        windowShiftLeft += pm.getWindowShiftLeft();

        Dimension windowSize = new Dimension(pm.getWindowWidth(), pm.getWindowHeight());
        webDriver.manage().window().setSize(windowSize);

        closer.register(webDriver);

        LOG.debug("webdriver.init", webDriver);

        return webDriver;
    }

}
