package io.sitoolkit.wt.app.ope2script;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import io.sitoolkit.wt.domain.guidance.GuidanceUtils;
import io.sitoolkit.wt.infra.MultiThreadUtils;
import io.sitoolkit.wt.infra.firefox.FirefoxManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.selenium.WebDriverInstaller;

public class FirefoxOpener {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(FirefoxOpener.class);

    private FirefoxManager ffManager = new FirefoxManager();

    private WebDriverInstaller webDriverInstaller = new WebDriverInstaller();

    private String guidanceFile = "guidance/guidance-ope2script.html";

    private String[] guidanceResources = new String[] { guidanceFile,
            "guidance/css/bootstrap.min.css", "guidance/css/style.css", "guidance/js/open.js" };

    private String baseUrl;

    public FirefoxOpener() {
        ffManager.init();
        webDriverInstaller.init();
        baseUrl = System.getProperty("baseUrl");
    }

    public static void main(String[] args) {
        System.exit(new FirefoxOpener().open());
    }

    /**
     * <ul>
     * <li>Firefoxがインストールされていない場合、インストールする
     * <li>Selenium IDEがインストールされていない場合、インストールする
     * <li>Firefoxを起動する
     * </ul>
     */
    public int open() {
        try {
            ffManager.switchEsr();
            webDriverInstaller.installGeckoDriver();

            GuidanceUtils.retrieve(guidanceResources);

            FirefoxProfile profile = new FirefoxProfile();
            profile.addExtension(ffManager.getSeleniumIdeUnarchivedDir().toFile());

            FirefoxBinary ffBinary = ffManager.getFirefoxBinary();

            LOG.info("firefox.start", ffBinary);

            FirefoxOptions ffOptions = new FirefoxOptions();
            ffOptions.setBinary(ffBinary);
            ffOptions.setProfile(profile);
            WebDriver driver = MultiThreadUtils
                    .submitWithProgress(() -> new FirefoxDriver(ffOptions));

            driver.get(GuidanceUtils.appendBaseUrl(guidanceFile, baseUrl));

            return 0;
        } catch (Exception e) {
            LOG.error("unexpected.exception", e);
            return 1;
        }
    }

    public int open(String baseUrl) {
        this.baseUrl = baseUrl;
        return open();
    }
}
