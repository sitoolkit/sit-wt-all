package io.sitoolkit.wt.app.ope2script;

import java.nio.file.Path;
import java.util.Objects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.sitoolkit.wt.domain.guidance.GuidanceUtils;
import io.sitoolkit.wt.infra.MultiThreadUtils;
import io.sitoolkit.wt.infra.chromium.ChromiumManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class ChromiumOpener {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(ChromiumOpener.class);

  private WebDriver driver;

  private ChromiumManager chromiumManager = new ChromiumManager();

  private String guidanceFile = "guidance/guidance-ope2script.html";

  private String[] guidanceResources =
      new String[] {
        guidanceFile,
        "guidance/css/bootstrap.min.css",
        "guidance/css/style.css",
        "guidance/js/open.js",
        "guidance/img/ic_stop_black_18dp_1x.png"
      };

  private String baseUrl;

  public ChromiumOpener() {
    chromiumManager.init();
    baseUrl = System.getProperty("baseUrl");
  }

  public static void main(String[] args) {
    System.exit(new ChromiumOpener().open());
  }

  public int open(String baseUrl) {
    this.baseUrl = baseUrl;
    return open();
  }

  public int open() {
    try {
      WebDriverManager.chromedriver().setup();

      GuidanceUtils.retrieve(guidanceResources);

      Path binary = chromiumManager.getBinary();
      ChromeOptions options = new ChromeOptions();
      options.addExtensions(chromiumManager.getSeleniumIde().toFile());
      options.setBinary(binary.toFile());

      LOG.info("chromium.start", binary.toAbsolutePath());

      driver = MultiThreadUtils.submitWithProgress(() -> new ChromeDriver(options));
      driver.get(GuidanceUtils.appendBaseUrl(guidanceFile, baseUrl));

      return 0;

    } catch (Exception e) {
      LOG.error("unexpected.exception", e);
      return 1;
    }
  }

  public void close() {
    LOG.info("chromium.stop");
    if (Objects.nonNull(driver)) {
      driver.quit();
    }
  }
}
