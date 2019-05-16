package io.sitoolkit.wt.infra;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import lombok.Getter;
import lombok.Setter;

@Configuration
@PropertySource(ignoreResourceNotFound = true,
    value = {"classpath:sit-wt-default.properties", "classpath:sit-wt.properties"})
public class PropertyManager {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(PropertyManager.class);

  @Value("${window.width}")
  private int windowWidth;

  @Value("${window.height}")
  private int windowHeight;

  @Value("${window.top}")
  private int windowTop;

  @Value("${window.left}")
  private int windowLeft;

  @Value("${window.shift.top}")
  private int windowShiftTop;

  @Value("${window.shift.left}")
  private int windowShiftLeft;

  @Value("${implicitlyWait}")
  private int implicitlyWait;

  @Value("${operationWait}")
  private int operationWait;

  @Value("${dialogWaitInSecond}")
  private int dialogWaitInSecond;

  @Value("${window.resize}")
  private boolean resizeWindow;

  @Value("${pagescript.dir}")
  private String pageScriptDir;

  @Setter
  @Value("${driver.type:}")
  private String driverType;

  @Value("${appium.address}")
  private String appiumAddress;

  @Value("${screenshot.mode}")
  private String screenshotMode;

  @Value("${screenshot.resize}")
  private boolean screenshotResize;

  @Value("${screenshot.padding.width}")
  private int screenshotPaddingWidth;

  @Value("${screenshot.padding.height}")
  private int screenshotPaddingHeight;

  @Value("${selenium.screenshot.pattern}")
  private String seleniumScreenshotPattern;

  @Value("${connection.properties}")
  private String connectionProperties;

  @Setter
  @Value("${baseUrl:}")
  private String baseUrl;

  @Value("${hubUrl}")
  private String hubUrl;

  @Setter
  @Value("${sitwt.debug:false}")
  private boolean isDebug;

  @Value("${sitwt.cli:true}")
  private boolean isCli;

  @Value("${wait.timeout}")
  private int timeout;

  @Value("${wait.waitSpan}")
  private int waitSpan;

  @Setter
  @Getter
  private Charset csvCharset = StandardCharsets.UTF_8;

  @Setter
  @Getter
  private boolean csvHasBOM = true;

  @Getter
  @Value("${sitwt.projectDirectory:#{null}}")
  private File projectDir;

  private Map<String, String> capabilities = new HashMap<>();

  private boolean isFirefoxDriver;

  private boolean isIeDriver;

  private boolean isEdgeDriver;

  private boolean isMsDriver;

  private boolean isRemoteDriver;

  private boolean isChromeDriver;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
    PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
    pspc.setIgnoreUnresolvablePlaceholders(true);
    return pspc;
  }

  @PostConstruct
  public void init() {
    capabilities = PropertyUtils.loadAsMap("/capabilities", true);

    setDriverFlags(toLowerCase(driverType), toLowerCase(capabilities.get("browserName")));

  }

  public void save(File dir) {
    PropertyUtils.save(this, new File(dir, "sit-wt.properties"));
  }

  void setDriverFlags(String driverType, String browserName) {
    isFirefoxDriver = equalsAny("firefox", driverType, browserName);
    isIeDriver = equalsAny("internet explorer", driverType, browserName) || "ie".equals(driverType);
    isEdgeDriver = equalsAny("edge", driverType, browserName);
    isMsDriver = isIeDriver || isEdgeDriver;
    isRemoteDriver = "remote".equals(driverType);
    isChromeDriver = equalsAny("chrome", driverType, browserName);
  }

  private String toLowerCase(String str) {
    return str == null ? "" : str.toLowerCase();
  }

  private boolean equalsAny(String str1, String... strs) {
    for (String str : strs) {
      if (str1.equals(str)) {
        return true;
      }
    }
    return false;
  }

  public int getWindowWidth() {
    return windowWidth;
  }

  public int getWindowHeight() {
    return windowHeight;
  }

  public int getImplicitlyWait() {
    return implicitlyWait;
  }

  public boolean isResizeWindow() {
    return resizeWindow;
  }

  public String getPageScriptDir() {
    return pageScriptDir;
  }

  public String getDriverType() {
    return driverType;
  }

  public String getDriverTypeInCapabilities() {
    return capabilities.get("browserName");
  }

  public boolean isRemoteDriver() {
    return isRemoteDriver;
  }

  public boolean isFirefoxDriver() {
    return isFirefoxDriver;
  }

  public boolean isEdgeDriver() {
    return isEdgeDriver;
  }

  public boolean isIeDriver() {
    return isIeDriver;
  }

  public boolean isMsDriver() {
    return isMsDriver;
  }

  public boolean isSafariDriver() {
    return "safari".equalsIgnoreCase(driverType);
  }

  public boolean isChromeDriver() {
    return isChromeDriver;
  }

  public URL getAppiumAddress() {
    try {
      return appiumAddress == null ? null : new URL(appiumAddress);
    } catch (MalformedURLException e) {
      throw new ConfigurationException("appium.address", e);
    }
  }

  public boolean isScreenshotResize() {
    return screenshotResize;
  }

  public int getScreenshotPaddingWidth() {
    return screenshotPaddingWidth;
  }

  public int getScreenshotPaddingHeight() {
    return screenshotPaddingHeight;
  }

  public Pattern getSeleniumScreenshotPattern() {
    try {
      return seleniumScreenshotPattern == null ? null : Pattern.compile(seleniumScreenshotPattern);
    } catch (PatternSyntaxException e) {
      throw new ConfigurationException("selenium.screenshot.pattern", e);
    }
  }

  public String getScreenthotMode() {
    return screenshotMode;
  }

  public String getConnectionProperties() {
    return connectionProperties;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getHubUrl() {
    return hubUrl;
  }

  public int getWindowTop() {
    return windowTop;
  }

  public int getWindowLeft() {
    return windowLeft;
  }

  public int getOperationWait() {
    return operationWait;
  }

  public int getWindowShiftTop() {
    return windowShiftTop;
  }

  public int getWindowShiftLeft() {
    return windowShiftLeft;
  }

  public int getDialogWaitInSecond() {
    return dialogWaitInSecond;
  }

  public Map<String, String> getCapabilities() {
    return capabilities;
  }

  public boolean isDebug() {
    return isDebug;
  }

  public boolean isCli() {
    return isCli;
  }

  public int getTimeout() {
    return timeout;
  }

  public int getWaitSpan() {
    return waitSpan;
  }

}
