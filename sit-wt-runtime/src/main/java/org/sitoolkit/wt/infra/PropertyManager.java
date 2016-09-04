package org.sitoolkit.wt.infra;

import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = { "classpath:sit-wt-default.properties",
        "classpath:sit-wt.properties" })
public class PropertyManager {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyManager.class);

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

    @Value("${pageobj.dir}")
    private String pageObjectDir;

    @Value("${driver.type}")
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

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${hubUrl}")
    private String hubUrl;

    private Map<String, String> capabilities = new HashMap<>();

    private boolean isFirefoxDriver;

    private boolean isIeDriver;

    private boolean isEdgeDriver;

    private boolean isMsDriver;

    private boolean isRemoteDriver;

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
        try (FileWriter writer = new FileWriter(new File(dir, "sit-wt.properties"))) {

            Map<String, String> map = new TreeMap<>();
            map.putAll(BeanUtils.describe(this));

            Properties prop = new Properties();

            for (Entry<String, String> entry : map.entrySet()) {
                prop.setProperty(entry.getKey(), StringUtils.defaultString(entry.getValue()));
            }

            prop.store(writer, "");

        } catch (Exception e) {
            LOG.error("プロパティの保存で例外が発生しました", e);
        }
    }

    public static void main(String[] args) {
    }

    void setDriverFlags(String driverType, String browserName) {
        isFirefoxDriver = equalsAny("firefox", driverType, browserName);
        isIeDriver = equalsAny("internet explorer", driverType, browserName)
                || "ie".equals(driverType);
        isEdgeDriver = equalsAny("edge", driverType, browserName);
        isMsDriver = isIeDriver || isEdgeDriver;
        isRemoteDriver = "remote".equals(driverType);
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

    public String getPageObjectDir() {
        return pageObjectDir;
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
            return seleniumScreenshotPattern == null ? null
                    : Pattern.compile(seleniumScreenshotPattern);
        } catch (PatternSyntaxException e) {
            throw new ConfigurationException("selenium.screenshot.pattern", e);
        }
    }

    public String getScreenthotMode() {
        return screenshotMode;
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

}
