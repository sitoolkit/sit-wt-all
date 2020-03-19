package io.sitoolkit.wt.infra.chromium;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.SystemUtils;
import org.codehaus.plexus.util.FileUtils;
import org.jsoup.UncheckedIOException;
import io.sitoolkit.util.buildtoolhelper.proxysetting.ProxySettingService;
import io.sitoolkit.wt.infra.MultiThreadUtils;
import io.sitoolkit.wt.infra.PropertyUtils;
import io.sitoolkit.wt.infra.SitRepository;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;

public class ChromiumManager {
  private static final SitLogger LOG = SitLoggerFactory.getLogger(ChromiumManager.class);

  private Path repositoryDir;
  private Path installDir;
  private String chromiumInstaller;
  private String chromiumUrl;
  private Path chromiumVersionFile;
  private Path chromiumBinary;
  private String seleniumIdeUrl;
  private String seleniumIdeInstaller;

  @PostConstruct
  public void init() {
    Map<String, String> prop = PropertyUtils.loadAsMap("/chromiuminstaller.properties", false);
    repositoryDir = Paths.get(SitRepository.getRepositoryPath(), "chromium");
    seleniumIdeInstaller = prop.get("seleniumIde.installer");
    seleniumIdeUrl = prop.get("seleniumIde.url");

    String version = prop.get("chromium.version");
    if (SystemUtils.IS_OS_WINDOWS) {
      installDir = Paths.get(repositoryDir.toString(), "chrome-win");
      chromiumInstaller = prop.get("win.chromium.installer");
      chromiumUrl = prop.get("win.chromium.url");
      chromiumVersionFile = Paths.get(installDir.toString(), version + ".manifest");
      chromiumBinary = Paths.get(installDir.toString(), "chrome.exe");

    } else if (SystemUtils.IS_OS_MAC) {
      installDir = Paths.get(repositoryDir.toString(), "chrome-mac");
      chromiumInstaller = prop.get("mac.chromium.installer");
      chromiumUrl = prop.get("mac.chromium.url");
      chromiumVersionFile =
          Paths.get(
              installDir.toString(),
              "Chromium.app/Contents/Frameworks/Chromium Framework.framework/Versions",
              version);
      chromiumBinary = Paths.get(installDir.toString(), "Chromium.app/Contents/MacOS/Chromium");

    } else {
      throw new UnsupportedOperationException(MessageManager.getMessage("os.unsupport"));
    }
  }

  public Path getBinary() {
    if (Files.exists(chromiumBinary)) {
      if (expectedVersion()) {
        LOG.info("chromium.exist", chromiumBinary.toString());

      } else {
        uninstall();
        install();
      }

    } else {
      LOG.info("chromium.sit.install");
      install();
    }

    return chromiumBinary;
  }

  private boolean expectedVersion() {
    return Files.exists(chromiumVersionFile);
  }

  private void install() {
    Path installer = Paths.get(repositoryDir.toString(), chromiumInstaller);

    if (Files.exists(installer)) {
      LOG.info("chromium.installer.exists", installer.toString());
    } else {
      try {
        ProxySettingService.getInstance().loadProxy();
        URL url = new URL(chromiumUrl);

        LOG.info("chromium.download", url, installer.toString());

        MultiThreadUtils.submitWithProgress(
            () -> {
              FileUtils.copyURLToFile(url, installer.toFile());
              return 0;
            });
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
    LOG.info("chromium.install");
    FileIOUtils.unarchive(installer.toFile(), repositoryDir.toFile());
  }

  private void uninstall() {
    try {
      LOG.info("chromium.uninstall");
      FileUtils.cleanDirectory(installDir.toFile());

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public Path getSeleniumIde() {
    Path ide = Paths.get(getSeleniumIdeRepository().toString(), seleniumIdeInstaller);

    if (Files.exists(ide)) {
      LOG.info("selenium.exist", ide.toString());

    } else {
      try {
        ProxySettingService.getInstance().loadProxy();
        URL url = new URL(seleniumIdeUrl);

        LOG.info("selenium.download", url, ide.toString());

        FileUtils.copyURLToFile(url, ide.toFile());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    return ide;
  }

  private Path getSeleniumIdeRepository() {
    Path repo = Paths.get(SitRepository.getRepositoryPath(), "selenium/ide");

    if (!Files.exists(repo)) {
      try {
        Files.createDirectories(repo);

      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    return repo;
  }
}
