package org.sitoolkit.wt.app.ope2script;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.sitoolkit.wt.domain.guidance.GuidanceUtils;
import org.sitoolkit.wt.infra.MultiThreadUtils;
import org.sitoolkit.wt.infra.firefox.FirefoxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxOpener {

    private static final Logger LOG = LoggerFactory.getLogger(FirefoxOpener.class);

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

            LOG.info("Firefoxを起動します {}", ffBinary);

            WebDriver driver = MultiThreadUtils
                    .submitWithProgress(() -> new FirefoxDriver(ffBinary, profile));

            String baseUrl = System.getProperty("baseUrl");
            driver.get(GuidanceUtils.appendBaseUrl(guidanceFile, baseUrl));

            // wait for Firefox window is closed
            ffBinary.waitFor();

            return 0;
        } catch (Exception e) {
            LOG.error("予期しない例外が発生しました", e);
            return 1;
        }
    }

}
