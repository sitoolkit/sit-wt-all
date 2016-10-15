package org.sitoolkit.wt.app.ope2script;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.internal.Executable;
import org.sitoolkit.wt.infra.SitRepository;
import org.sitoolkit.wt.infra.process.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxManager {

    private static final Logger LOG = LoggerFactory.getLogger(FirefoxManager.class);

    public FirefoxManager() {
    }

    public static void main(String[] args) {
        System.exit(new FirefoxManager().open());
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
            FirefoxBinaryExt ffBinary = getFirefoxBinary();

            if (ffBinary == null) {

                installFirefox();

                reRunMySelf();

            } else {

                if (checkSeleniumIdeInstalledWindows()) {

                    LOG.info("Firefoxを起動します");
                    ProcessUtils.execute(false, ffBinary.getExecutable().getPath(), "-foreground");

                } else {

                    installSeleniumIde(ffBinary);

                }

            }
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    private void installSeleniumIde(FirefoxBinaryExt ffBinary) {
        File repo = new File(SitRepository.getRepositoryPath(), "selenium/ide");

        if (!repo.exists()) {
            repo.mkdirs();
        }

        File xpi = new File(repo, "selenium_ide-2.9.1-fx.xpi");

        if (!xpi.exists()) {

            try {

                URL xpiUrl = new URL(
                        "https://addons.mozilla.org/firefox/downloads/latest/selenium-ide/addon-2079-latest.xpi?src=dp-btn-primary");

                LOG.info("Selenium IDEをダウンロードします {} -> {}", xpiUrl, xpi.getAbsolutePath());

                FileUtils.copyURLToFile(xpiUrl, xpi);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        LOG.info("FirefoxにSelenium IDEをインストールします");
        ProcessUtils.execute(false, ffBinary.getExecutable().getPath(), xpi.getAbsolutePath(),
                "-foreground");
    }

    /**
     * 自クラスのmainメソッドを別のJavaプロセスで実行します。
     */
    private void reRunMySelf() {
        String javaHome = System.getProperty("java.home");
        String javaCmd = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        ProcessUtils.execute(javaCmd, "-cp", classpath, getClass().getName());
    }

    private boolean checkSeleniumIdeInstalled(String profilePath) {

        LOG.debug("Selenium IDEがインストールされているかチェックします");

        File ffProfile = getFfProfileDir();

        File profile = null;

        for (File f : ffProfile.listFiles()) {
            if (f.getName().endsWith(".default")) {
                profile = f;
            }
        }

        try {
            String extensions = FileUtils.readFileToString(new File(profile, "extensions.json"),
                    "UTF-8");

            boolean installed = extensions.contains("Selenium IDE");

            LOG.debug("Selenium IDEがインストールされて{}", installed ? "います" : "いません");

            return installed;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean checkSeleniumIdeInstalledWindows() {
        return checkSeleniumIdeInstalled("AppData/Roaming/Mozilla/Firefox/Profiles");
    }

    private boolean checkSeleniumIdeInstalledMacOs() {
        return checkSeleniumIdeInstalled("Library/Application Support/Firefox/Profiles");
    }

    private File getFfProfileDir() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new File(System.getProperty("user.home"),
                    "AppData/Roaming/Mozilla/Firefox/Profiles");
        } else if (SystemUtils.IS_OS_MAC) {
            return new File(System.getProperty("user.home"),
                    "Library/Application Support/Firefox/Profiles");
        } else {
            throw new UnsupportedOperationException("");
        }

    }

    /**
     * Firefoxの実行ファイルを返します。 Firefoxがインストールされていない場合はnullを返します。
     *
     * @return Firefoxの実行ファイル
     */
    private FirefoxBinaryExt getFirefoxBinary() {
        try {

            return new FirefoxBinaryExt();

        } catch (WebDriverException e) {
            if (e.getMessage().startsWith("Cannot find firefox binary in PATH")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void installFirefox() {
        File repo = new File(SitRepository.getRepositoryPath(), "firefox");

        if (!repo.exists()) {
            repo.mkdir();
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            installFirefoxWindows(repo);
        } else if (SystemUtils.IS_OS_MAC) {
            installFirefoxMacOs(repo);
        } else {
            throw new UnsupportedOperationException("サポートされていないOSです");
        }
    }

    private void installFirefoxWindows(File repo) {
        File ffInstaller = new File(repo, "Firefox Setup 48.0.1.exe");

        if (!ffInstaller.exists()) {
            try {
                URL url = new URL(
                        "https://ftp.mozilla.org/pub/firefox/releases/48.0.1/win64/ja/Firefox%20Setup%2048.0.1.exe");

                LOG.info("Firefoxをダウンロードします {} -> {}", url, ffInstaller.getAbsolutePath());

                FileUtils.copyURLToFile(url, ffInstaller);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        LOG.info("Firefoxをインストールします");
        ProcessUtils.execute(ffInstaller.getAbsolutePath(), "-ms");

    }

    private void installFirefoxMacOs(File repo) {

        File ffInstaller = new File(repo, "Firefox 48.0.1.dmg");

        if (!ffInstaller.exists()) {
            try {
                URL url = new URL(
                        "https://ftp.mozilla.org/pub/firefox/releases/48.0.1/mac/ja-JP-mac/Firefox%2048.0.1.dmg");

                LOG.info("Firefoxをダウンロードします {} -> {}", url, ffInstaller.getAbsolutePath());

                FileUtils.copyURLToFile(url, ffInstaller);

                ProcessUtils.execute("chmod", "777", ffInstaller.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        LOG.info("Firefoxのインストーラーをマウントします");
        ProcessUtils.execute("hdiutil", "attach", ffInstaller.getAbsolutePath());

        LOG.info("Firefoxをインストールします");
        ProcessUtils.execute("cp", "-R", "/Volumes/Firefox/Firefox.app", "/Applications");

        LOG.info("Firefoxのインストーラーをアンマウントします");
        ProcessUtils.execute("hdiutil", "detach", "/Volumes/Firefox");

    }

    private class FirefoxBinaryExt extends FirefoxBinary {

        public Executable getExecutable() {
            return super.getExecutable();
        }

    }
}
