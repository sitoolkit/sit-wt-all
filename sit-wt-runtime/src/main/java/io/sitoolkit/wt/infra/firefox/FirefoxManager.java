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
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.zeroturnaround.zip.ZipUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySettingService;
import io.sitoolkit.wt.infra.JsonUtils;
import io.sitoolkit.wt.infra.MultiThreadUtils;
import io.sitoolkit.wt.infra.PropertyUtils;
import io.sitoolkit.wt.infra.SitRepository;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.process.ProcessUtils;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.util.infra.UnExpectedException;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;

public class FirefoxManager {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(FirefoxManager.class);

  private String firefoxVersion;
  private String winFirefoxDownloadUrl;
  private String winFirefoxInstallFile;
  private String macFirefoxDownloadUrl;
  private String macFirefoxInstallFile;
  private String seleniumIdeUrl;
  private String seleniumIdeXpi;
  private String seleniumIdeVersion;
  private Map<String, String> prop;
  private String installDir;
  private String installIni;

  @PostConstruct
  public void init() {
    prop = PropertyUtils.loadAsMap("/firefoxinstaller-default.properties", false);
    prop.putAll(PropertyUtils.loadAsMap("/firefoxinstaller.properties", true));

    firefoxVersion = prop.get("firefox.version");
    winFirefoxDownloadUrl = prop.get("win.firefox.downloadUrl");
    winFirefoxInstallFile = prop.get("win.firefox.installFile");
    macFirefoxDownloadUrl = prop.get("mac.firefox.downloadUrl");
    macFirefoxInstallFile = prop.get("mac.firefox.installFile");
    seleniumIdeVersion = prop.get("seleniumIde.version");
    seleniumIdeUrl = prop.get("seleniumIde.url");
    seleniumIdeXpi = prop.get("seleniumIde.xpi");
    installDir = "firefox";
    installIni = "ff-inst.ini";
  }

  public void switchEsr() {
    winFirefoxDownloadUrl = prop.get("win.firefox.esr.downloadUrl");
    winFirefoxInstallFile = prop.get("win.firefox.esr.installFile");
    macFirefoxDownloadUrl = prop.get("mac.firefox.esr.downloadUrl");
    macFirefoxInstallFile = prop.get("mac.firefox.esr.installFile");
    installDir = "firefox-esr";
    installIni = "ff-esr-inst.ini";
  }

  public FirefoxDriver startWebDriver(DesiredCapabilities capabilities, List<String> arguments) {
    return MultiThreadUtils.submitWithProgress(() -> {
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

  public Path getSeleniumIdeUnarchivedDir() {
    Path repo = Paths.get(SitRepository.getRepositoryPath(), "selenium/ide");

    if (!Files.exists(repo)) {
      try {
        Files.createDirectories(repo);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    Path xpi = Paths.get(repo.toString(), seleniumIdeXpi);

    if (Files.exists(xpi)) {
      LOG.info("selenium.exist", xpi.toString());
    } else {

      try {
        if (!seleniumIdeVersion.equals(getSeleniumIdeInstallVersion())) {
          uninstallSeleniumIde();
        }

        ProxySettingService.getInstance().loadProxy();

        URL xpiUrl = new URL(seleniumIdeUrl);

        LOG.info("selenium.download", xpiUrl, xpi.toString());

        FileUtils.copyURLToFile(xpiUrl, xpi.toFile());
        ZipUtil.unpack(xpi.toFile(), repo.toFile());

      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (Exception exp) {
        throw new RuntimeException(exp);
      }
    }

    return repo;
  }

  protected String getSeleniumIdeInstallVersion() {
    Path manifest = Paths.get(SitRepository.getRepositoryPath(), "selenium/ide/manifest.json");
    if (Files.exists(manifest)) {
      JsonNode node = JsonUtils.readTree(manifest);
      return node.get("version").asText();
    }

    return null;
  }

  public void uninstallSeleniumIde() {
    try {
      FileUtils
          .cleanDirectory(Paths.get(SitRepository.getRepositoryPath(), "selenium/ide").toFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected Path getFirefoxBinaryFile() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return Paths.get(SitRepository.getRepositoryPath(), installDir, "runtime/firefox.exe");
    } else if (SystemUtils.IS_OS_MAC) {
      return Paths.get(SitRepository.getRepositoryPath(), installDir,
          "runtime/Firefox.app/Contents/MacOS/firefox-bin");
    } else {
      throw new UnsupportedOperationException(MessageManager.getMessage("os.unsupport"));
    }
  }

  protected void installFirefox() {
    Path repo = Paths.get(SitRepository.getRepositoryPath(), installDir);

    if (!Files.exists(repo)) {
      try {
        Files.createDirectories(repo);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    if (SystemUtils.IS_OS_WINDOWS) {
      installFirefoxWindows(repo);
    } else if (SystemUtils.IS_OS_MAC) {
      installFirefoxMacOs(repo);
    } else {
      throw new UnsupportedOperationException(MessageManager.getMessage("os.unsupport"));
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

        MultiThreadUtils.submitWithProgress(() -> {
          FileUtils.copyURLToFile(url, ffInstaller.toFile());
          return 0;
        });

      } catch (IOException e) {
        throw new RuntimeException(MessageManager.getMessage("firefox.download.exception"), e);
      } catch (Exception exp) {
        throw new RuntimeException(MessageManager.getMessage("firefox.download.exception"), exp);
      }
    }

    LOG.info("firefox.install");

    try {
      Path iniFile = Files.createTempFile("ff-inst", ".ini");

      try {
        FileIOUtils.sysRes2file(installIni, iniFile, true);

        MultiThreadUtils.submitWithProgress(() -> {
          ProcessUtils.execute(ffInstaller.toString(), "/INI=" + iniFile.toString());
          return 0;
        });
      } catch (Exception e) {
        throw new RuntimeException("firefox.install.exception", e);
      }
    } catch (IOException e) {
      throw new RuntimeException("firefox.install.exception", e);
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
        throw new RuntimeException(e);
      } catch (Exception exp) {
        throw new RuntimeException(exp);
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

    } catch (IOException | UnExpectedException e) {
      throw new RuntimeException(e);

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
      throw new RuntimeException(e);
    }
  }

  protected Path getFirefoxIni() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return Paths.get(SitRepository.getRepositoryPath(), installDir, "runtime/application.ini");
    } else if (SystemUtils.IS_OS_MAC) {
      return Paths.get(SitRepository.getRepositoryPath(), installDir,
          "runtime/Firefox.app/Contents/Resources/application.ini");
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
    Path ffUninstaller =
        Paths.get(SitRepository.getRepositoryPath(), installDir, "runtime/uninstall/helper.exe");
    MultiThreadUtils.submitWithProgress(() -> {
      ProcessUtils.execute(ffUninstaller.toString(), "-ms");
      return 0;
    });
  }

  protected void uninstallFirefoxMacOs() {
    Path ffRuntime = Paths.get(SitRepository.getRepositoryPath(), installDir, "runtime");
    try {
      FileUtils.deleteDirectory(ffRuntime.toFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
