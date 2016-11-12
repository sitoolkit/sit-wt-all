package org.sitoolkit.wt.app.ope2script;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.sitoolkit.wt.infra.firefox.FirefoxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxOpener {

    private static final Logger LOG = LoggerFactory.getLogger(FirefoxOpener.class);

    private FirefoxManager ffManager = new FirefoxManager();

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
            FirefoxProfile profile = new FirefoxProfile();
            profile.addExtension(ffManager.getSeleniumIdeUnarchivedDir());

            FirefoxBinary ffBinary = ffManager.getFirefoxBinary();

            LOG.info("Firefoxを起動します {}", ffBinary);

            WebDriver driver = new FirefoxDriver(ffBinary, profile);
            String url = System.getProperty("url");
            if (StringUtils.isNotEmpty(url)) {
                driver.get(url);
            }

            // wait for Firefox window is closed
            ffBinary.waitFor();

            return 0;
        } catch (Exception e) {
            LOG.error("予期しない例外が発生しました", e);
            return 1;
        }
    }

}
