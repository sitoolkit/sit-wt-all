package io.sitoolkit.wt.infra.firefox;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jsoup.UncheckedIOException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySettingService;
import io.sitoolkit.wt.infra.MultiThreadUtils;
import io.sitoolkit.wt.infra.PropertyUtils;
import io.sitoolkit.wt.infra.SitRepository;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.process.ProcessUtils;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;

public class FirefoxManager {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(FirefoxManager.class);

  private String firefoxVersion;
  private String winFirefoxDownloadUrl;
  private String winFirefoxInstallFile;
  private String macFirefoxDownloadUrl;
  private String macFirefoxInstallFile;
  private String installDir;
  private String installIni;

  private String unsupportMsg = MessageManager.getMessage("os.unsupport");

  @PostConstruct
  public void init() {
    Map<String, String> prop =
        PropertyUtils.loadAsMap("/firefoxinstaller-default.properties", false);
    prop.putAll(PropertyUtils.loadAsMap("/firefoxinstaller.properties", true));

    firefoxVersion = prop.get("firefox.version");
    winFirefoxDownloadUrl = prop.get("win.firefox.downloadUrl");
    winFirefoxInstallFile = prop.get("win.firefox.installFile");
    macFirefoxDownloadUrl = prop.get("mac.firefox.downloadUrl");
    macFirefoxInstallFile = prop.get("mac.firefox.installFile");
    installDir = "firefox";
    installIni = "ff-inst.ini";
  }

  public FirefoxDriver startWebDriver(DesiredCapabilities capabilities, List<String> arguments) {
    return MultiThreadUtils.submitWithProgress(
        () -> {
          FirefoxOptions options = new FirefoxOptions(capabilities);
          options.setBinary(getFirefoxBinary());
          options.addArguments(arguments);
          return new FirefoxDriver(options);
        });
  }

  public FirefoxBinary getFirefoxBinary() {
    Path ffBinaryFile = getFirefoxBinaryFile();

    if (Files.exists(ffBinaryFile)) {

      if (firefoxVersion.equals(getFirefoxVersion())) {
        LOG.info("firefox.exist", ffBinaryFile.toString());

      } else {
        uninstallFirefox();
        installFirefox();
      }

    } else {

      LOG.info("firefox.sit.install");
      installFirefox();
    }

    return new FirefoxBinary(ffBinaryFile.toFile());
  }

  protected Path getFirefoxBinaryFile() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return Paths.get(SitRepository.getRepositoryPath(), installDir, "runtime/firefox.exe");
    } else if (SystemUtils.IS_OS_MAC) {
      return Paths.get(
          SitRepository.getRepositoryPath(),
          installDir,
          "runtime/Firefox.app/Contents/MacOS/firefox-bin");
    } else {
      throw new UnsupportedOperationException(unsupportMsg);
    }
  }

  protected void installFirefox() {
    Path repo = Paths.get(SitRepository.getRepositoryPath(), installDir);

    if (!Files.exists(repo)) {
      try {
        Files.createDirectories(repo);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    if (SystemUtils.IS_OS_WINDOWS) {
      installFirefoxWindows(repo);
    } else if (SystemUtils.IS_OS_MAC) {
      installFirefoxMacOs(repo);
    } else {
      throw new UnsupportedOperationException(unsupportMsg);
    }
  }

  protected void installFirefoxWindows(Path repo) {
    Path ffInstaller = Paths.get(repo.toString(), winFirefoxInstallFile);

    if (Files.exists(ffInstaller)) {
      LOG.info("ffInstaller.exists", ffInstaller.toString());
    } else {
      try {
        ProxySettingService.getInstance().loadProxy();

        URL url = new URL(winFirefoxDownloadUrl);

        LOG.info("firefox.download", url, ffInstaller.toString());

        MultiThreadUtils.submitWithProgress(
            () -> {
              FileUtils.copyURLToFile(url, ffInstaller.toFile());
              return 0;
            });

      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    LOG.info("firefox.install");

    try {
      Path iniFile = Files.createTempFile("ff-inst", ".ini");

      FileIOUtils.sysRes2file(installIni, iniFile, true);

      MultiThreadUtils.submitWithProgress(
          () -> {
            ProcessUtils.execute(ffInstaller.toString(), "/INI=" + iniFile.toString());
            return 0;
          });
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected void installFirefoxMacOs(Path repo) {
    Path ffInstaller = Paths.get(repo.toString(), macFirefoxInstallFile);

    if (Files.exists(ffInstaller)) {
      LOG.info("ffInstaller.exists", ffInstaller.toString());
    } else {
      try {
        ProxySettingService.getInstance().loadProxy();

        URL url = new URL(macFirefoxDownloadUrl);

        LOG.info("firefox.download", url, ffInstaller.toString());

        FileUtils.copyURLToFile(url, ffInstaller.toFile());

        ProcessUtils.execute("chmod", "777", ffInstaller.toString());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    LOG.info("firefox.mount");
    ProcessUtils.execute("hdiutil", "attach", ffInstaller.toString());

    Path mountedFf = Paths.get("/Volumes/Firefox/Firefox.app");
    Path ffRuntime = Paths.get(repo.toString(), "runtime");

    LOG.info("firefox.install2", ffRuntime.toString());
    try {
      if (!Files.exists(ffRuntime)) {
        Files.createDirectories(ffRuntime);
      }
      FileIOUtils.copyDirectoryWithPermission(mountedFf, ffRuntime);

    } catch (IOException e) {
      throw new UncheckedIOException(e);

    } finally {
      LOG.info("firefox.unmount");
      ProcessUtils.execute("hdiutil", "detach", "/Volumes/Firefox");
    }
  }

  protected String getFirefoxVersion() {
    Properties ffProp = new Properties();
    Path firefoxIni = getFirefoxIni();

    try (InputStream iniInStream = Files.newInputStream(firefoxIni)) {
      ffProp.load(iniInStream);
      return ffProp.getProperty("Version");

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected Path getFirefoxIni() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return Paths.get(SitRepository.getRepositoryPath(), installDir, "runtime/application.ini");
    } else if (SystemUtils.IS_OS_MAC) {
      return Paths.get(
          SitRepository.getRepositoryPath(),
          installDir,
          "runtime/Firefox.app/Contents/Resources/application.ini");
    } else {
      throw new UnsupportedOperationException(unsupportMsg);
    }
  }

  protected void uninstallFirefox() {
    LOG.info("firefox.uninstall");

    if (SystemUtils.IS_OS_WINDOWS) {
      uninstallFirefoxWindows();
    } else if (SystemUtils.IS_OS_MAC) {
      uninstallFirefoxMacOs();
    } else {
      throw new UnsupportedOperationException(unsupportMsg);
    }
  }

  protected void uninstallFirefoxWindows() {
    Path ffUninstaller =
        Paths.get(SitRepository.getRepositoryPath(), installDir, "runtime/uninstall/helper.exe");
    MultiThreadUtils.submitWithProgress(
        () -> {
          ProcessUtils.execute(ffUninstaller.toString(), "-ms");
          return 0;
        });
  }

  protected void uninstallFirefoxMacOs() {
    Path ffRuntime = Paths.get(SitRepository.getRepositoryPath(), installDir, "runtime");
    try {
      FileUtils.deleteDirectory(ffRuntime.toFile());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
