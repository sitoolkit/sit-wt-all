package org.sitoolkit.wt.infra.firefox;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.sitoolkit.wt.infra.MultiThreadUtils;
import org.sitoolkit.wt.infra.SitRepository;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.sitoolkit.wt.infra.process.ProcessUtils;
import org.sitoolkit.wt.infra.resource.MessageManager;
import org.sitoolkit.wt.util.app.proxysetting.ProxySettingService;
import org.zeroturnaround.zip.ZipUtil;

public class FirefoxManager {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(FirefoxManager.class);

    public FirefoxDriver startWebDriver(DesiredCapabilities capabilities) {
        return MultiThreadUtils.submitWithProgress(() -> {
            FirefoxOptions options = new FirefoxOptions(capabilities);
            options.setBinary(getFirefoxBinary());
            return new FirefoxDriver(options);
        });

    }

    public FirefoxBinary getFirefoxBinary() {
        File ffBinaryFile = getFirefoxBinaryFile();

        if (ffBinaryFile.exists()) {

            LOG.info("firefox.exist", ffBinaryFile.getAbsolutePath());

        } else {

            LOG.info("firefox.sit.install");
            installFirefox();

        }

        return new FirefoxBinary(ffBinaryFile);
    }

    public File getSeleniumIdeUnarchivedDir() {
        File repo = new File(SitRepository.getRepositoryPath(), "selenium/ide");

        if (!repo.exists()) {
            repo.mkdirs();
        }

        File xpi = new File(repo, "selenium_ide-2.9.1-fx.xpi");

        if (xpi.exists()) {
            LOG.info("selenium.exist", xpi.getAbsolutePath());
        } else {

            try {
                ProxySettingService.getInstance().loadProxy();

                // TODO 外部化
                URL xpiUrl = new URL(
                        "https://addons.mozilla.org/firefox/downloads/latest/selenium-ide/addon-2079-latest.xpi?src=dp-btn-primary");

                LOG.info("selenium.download", xpiUrl, xpi.getAbsolutePath());

                FileUtils.copyURLToFile(xpiUrl, xpi);
                ZipUtil.unpack(xpi, repo);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception exp) {
                throw new RuntimeException(exp);
            }
        }

        return repo;
    }

    protected File getFirefoxBinaryFile() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new File(SitRepository.getRepositoryPath(), "firefox/runtime/firefox.exe");
        } else if (SystemUtils.IS_OS_MAC) {
            return new File(SitRepository.getRepositoryPath(),
                    "firefox/runtime/Firefox.app/Contents/MacOS/firefox-bin");
        } else {
            throw new UnsupportedOperationException(MessageManager.getMessage("os.unsupport"));
        }
    }

    protected void installFirefox() {
        File repo = new File(SitRepository.getRepositoryPath(), "firefox");

        if (!repo.exists()) {
            repo.mkdir();
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            installFirefoxWindows(repo);
        } else if (SystemUtils.IS_OS_MAC) {
            installFirefoxMacOs(repo);
        } else {
            throw new UnsupportedOperationException(MessageManager.getMessage("os.unsupport"));
        }
    }

    protected void installFirefoxWindows(File repo) {
        File ffInstaller = new File(repo, "Firefox Setup 60.0.2.exe");

        if (ffInstaller.exists()) {
            LOG.info("ffInstaller.exists", ffInstaller.getAbsolutePath());
        } else {
            try {
                ProxySettingService.getInstance().loadProxy();

                // TODO 外部化
                URL url = new URL(
                        "https://ftp.mozilla.org/pub/firefox/releases/60.0.2/win64/ja/Firefox%20Setup%2060.0.2.exe");

                LOG.info("firefox.download", url, ffInstaller.getAbsolutePath());

                MultiThreadUtils.submitWithProgress(() -> {
                    FileUtils.copyURLToFile(url, ffInstaller);
                    return 0;
                });

            } catch (IOException e) {
                throw new RuntimeException(MessageManager.getMessage("firefox.download.exception"),
                        e);
            } catch (Exception exp) {
                throw new RuntimeException(MessageManager.getMessage("firefox.download.exception"),
                        exp);
            }
        }

        LOG.info("firefox.install");

        try {
            File iniFile = File.createTempFile("ff-inst", ".ini");
            iniFile.deleteOnExit();

            FileUtils.copyInputStreamToFile(ClassLoader.getSystemResourceAsStream("ff-inst.ini"),
                    iniFile);

            MultiThreadUtils.submitWithProgress(() -> {
                ProcessUtils.execute(ffInstaller.getAbsolutePath(),
                        "/INI=" + iniFile.getAbsolutePath());
                return 0;
            });
        } catch (IOException e) {
            throw new RuntimeException("firefox.install.exception", e);
        }

    }

    protected void installFirefoxMacOs(File repo) {

        File ffInstaller = new File(repo, "Firefox 60.0.2.dmg");

        if (ffInstaller.exists()) {
            LOG.info("ffInstaller.exists", ffInstaller.getAbsolutePath());
        } else {
            try {
                ProxySettingService.getInstance().loadProxy();

                // TODO 外部化
                URL url = new URL(
                        "https://ftp.mozilla.org/pub/firefox/releases/60.0.2/mac/ja-JP-mac/Firefox%2060.0.2.dmg");

                LOG.info("firefox.download", url, ffInstaller.getAbsolutePath());

                FileUtils.copyURLToFile(url, ffInstaller);

                ProcessUtils.execute("chmod", "777", ffInstaller.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception exp) {
                throw new RuntimeException(exp);
            }
        }

        LOG.info("firefox.mount");
        ProcessUtils.execute("hdiutil", "attach", ffInstaller.getAbsolutePath());

        File mountedFf = new File("/Volumes/Firefox/Firefox.app");
        File ffRuntime = new File(repo, "runtime");

        LOG.info("firefox.install2", ffRuntime.getAbsolutePath());
        // たまにcp -Rコマンドが失敗してFirefox.appがコピーされないので3回リトライ
        for (int i = 0; i < 3; i++) {

            MultiThreadUtils.submitWithProgress(() -> {
                ProcessUtils.execute("cp", "-R", mountedFf.getAbsolutePath(),
                        ffRuntime.getAbsolutePath());
                return 0;
            });

            if (new File(ffRuntime, "Firefox.app").exists()) {
                break;
            }
        }

        LOG.info("firefox.unmount");
        ProcessUtils.execute("hdiutil", "detach", "/Volumes/Firefox");

    }

}
