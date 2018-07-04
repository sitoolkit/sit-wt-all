package org.sitoolkit.wt.infra.firefox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.sitoolkit.wt.infra.MultiThreadUtils;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.sitoolkit.wt.infra.SitRepository;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.sitoolkit.wt.infra.process.ProcessUtils;
import org.sitoolkit.wt.infra.resource.MessageManager;
import org.sitoolkit.wt.util.app.proxysetting.ProxySettingService;
import org.zeroturnaround.zip.ZipUtil;

public class FirefoxManager {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(FirefoxManager.class);

    private String firefoxVersion;
    private String winFirefoxDownloadUrl;
    private String winFirefoxInstallFile;
    private String macFirefoxDownloadUrl;
    private String macFirefoxInstallFile;
    private String seleniumIdeUrl;
    private String seleniumIdeXpi;

    @PostConstruct
    public void init() {
        Map<String, String> prop = PropertyUtils.loadAsMap("/firefoxinstaller-default.properties",
                false);
        prop.putAll(PropertyUtils.loadAsMap("/firefoxinstaller.properties", true));

        firefoxVersion = prop.get("firefox.version");
        winFirefoxDownloadUrl = prop.get("win.firefox.downloadUrl");
        winFirefoxInstallFile = prop.get("win.firefox.installFile");
        macFirefoxDownloadUrl = prop.get("mac.firefox.downloadUrl");
        macFirefoxInstallFile = prop.get("mac.firefox.installFile");
        seleniumIdeUrl = prop.get("seleniumIde.url");
        seleniumIdeXpi = prop.get("seleniumIde.xpi");
    }

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

            if (firefoxVersion.equals(getFirefoxVersion())) {
                LOG.info("firefox.exist", ffBinaryFile.getAbsolutePath());

            } else {
                uninstallFirefox();
                installFirefox();
            }

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

        File xpi = new File(repo, seleniumIdeXpi);

        if (xpi.exists()) {
            LOG.info("selenium.exist", xpi.getAbsolutePath());
        } else {

            try {
                ProxySettingService.getInstance().loadProxy();

                URL xpiUrl = new URL(seleniumIdeUrl);

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
        File ffInstaller = new File(repo, winFirefoxInstallFile);

        if (ffInstaller.exists()) {
            LOG.info("ffInstaller.exists", ffInstaller.getAbsolutePath());
        } else {
            try {
                ProxySettingService.getInstance().loadProxy();

                URL url = new URL(winFirefoxDownloadUrl);

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

        File ffInstaller = new File(repo, macFirefoxInstallFile);

        if (ffInstaller.exists()) {
            LOG.info("ffInstaller.exists", ffInstaller.getAbsolutePath());
        } else {
            try {
                ProxySettingService.getInstance().loadProxy();

                URL url = new URL(macFirefoxDownloadUrl);

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

    protected String getFirefoxVersion() {
        try {
            Properties ffProp = new Properties();
            File firefoxIni = getFirefoxIni();
            ffProp.load(new FileInputStream(firefoxIni));
            return ffProp.getProperty("Version");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File getFirefoxIni() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new File(SitRepository.getRepositoryPath(), "firefox/runtime/application.ini");
        } else if (SystemUtils.IS_OS_MAC) {
            return new File(SitRepository.getRepositoryPath(),
                    "firefox/runtime/Firefox.app/Contents/MacOS/application.ini");
        } else {
            throw new UnsupportedOperationException(MessageManager.getMessage("os.unsupport"));
        }
    }

    protected void uninstallFirefox() {
        LOG.info("firefox.uninstall");

        if (SystemUtils.IS_OS_WINDOWS) {
            uninstallFirefoxWindows();
        } else if (SystemUtils.IS_OS_MAC) {
            uninstallFirefoxMacOs();
        } else {
            throw new UnsupportedOperationException(MessageManager.getMessage("os.unsupport"));
        }
    }

    protected void uninstallFirefoxWindows() {
        File ffUninstaller = new File(SitRepository.getRepositoryPath(),
                "firefox/runtime/uninstall/helper.exe");
        MultiThreadUtils.submitWithProgress(() -> {
            ProcessUtils.execute(ffUninstaller.getAbsolutePath(), "-ms");
            return 0;
        });
    }

    protected void uninstallFirefoxMacOs() {
        File ffRuntime = new File(SitRepository.getRepositoryPath(), "firefox/runtime");
        MultiThreadUtils.submitWithProgress(() -> {
            ProcessUtils.execute("rm", "-f", ffRuntime.getAbsolutePath());
            return 0;
        });

    }

}
