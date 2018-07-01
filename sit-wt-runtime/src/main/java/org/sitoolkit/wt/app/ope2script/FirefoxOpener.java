package org.sitoolkit.wt.app.ope2script;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.XpiDriverService;
import org.sitoolkit.wt.domain.guidance.GuidanceUtils;
import org.sitoolkit.wt.infra.MultiThreadUtils;
import org.sitoolkit.wt.infra.firefox.FirefoxManager;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;

public class FirefoxOpener {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(FirefoxOpener.class);

    private FirefoxManager ffManager = new FirefoxManager();

    private String guidanceFile = "guidance/guidance-ope2script.html";

    private String[] guidanceResources = new String[] { guidanceFile,
            "guidance/css/bootstrap.min.css", "guidance/css/style.css", "guidance/js/open.js" };

    public FirefoxOpener() {
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

            GuidanceUtils.retrieve(guidanceResources);

            FirefoxProfile profile = new FirefoxProfile();
            profile.addExtension(ffManager.getSeleniumIdeUnarchivedDir());

            FirefoxBinary ffBinary = ffManager.getFirefoxBinary();

            LOG.info("firefox.start", ffBinary);

            WebDriver driver = MultiThreadUtils.submitWithProgress(() -> new FirefoxDriver(
                    XpiDriverService.builder().withBinary(ffBinary).withProfile(profile).build()));

            String baseUrl = System.getProperty("baseUrl");
            driver.get(GuidanceUtils.appendBaseUrl(guidanceFile, baseUrl));

            // wait for Firefox window is closed
            ffBinary.waitFor();

            return 0;
        } catch (Exception e) {
            LOG.error("unexpected.exception", e);
            return 1;
        }
    }

}
