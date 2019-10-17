package io.sitoolkit.wt.domain.tester;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import io.sitoolkit.wt.app.httpserver.SitHttpServerStart;
import io.sitoolkit.wt.app.httpserver.SitHttpServerStop;
import io.sitoolkit.wt.app.sample.SampleGenerator;
import io.sitoolkit.wt.domain.operation.selenium.OpenOperation;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.PropertyManager;

public abstract class TestBase extends SitTesterTestBase {

  @Resource
  PropertyManager pm;

  private final int testHttpServerPort = 8280;
  private final String contentDir = "src/main/resources/webapp";
  private final String localhostUrl = "http://localhost:" + testHttpServerPort;

  private boolean httpServerOwner = false;
  private String orgBaseUrl = "";

  @BeforeClass
  public static void initialize() throws IOException {
    SampleGenerator.generate();
  }

  @Before
  public void setUp() {
    super.setUp();
    if (openVelocityTemplate() && needToStartServer()) {
      SitHttpServerStart.startServer(testHttpServerPort, contentDir);
      orgBaseUrl = pm.getBaseUrl();
      pm.setBaseUrl(localhostUrl);
      httpServerOwner = true;
    }
  }

  private boolean needToStartServer() {
    try {
      URL url = new URL(localhostUrl + "/index.html");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(100);
      connection.connect();
      connection.getInputStream().close();
      return false;

    } catch (IOException e) {
      return true;
    }
  }

  @After
  public void tearDown() {
    super.tearDown();
    if (httpServerOwner) {
      SitHttpServerStop.stopServer(testHttpServerPort);
      pm.setBaseUrl(orgBaseUrl);
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  private boolean openVelocityTemplate() {
    TestStep testStep = tester.getTestScript().getTestStepList().stream()
        .filter(t -> t.getOperation() instanceof OpenOperation).findFirst().orElse(null);

    if (Objects.nonNull(testStep)) {

      String openUrl = testStep.getLocator().getValue();
      if (StringUtils.startsWith(openUrl, "http")) {
        return isLocalVelocityTemplateUrl(openUrl);

      } else {
        String velocityTemplateName = StringUtils.replace(openUrl, "html", "vm");
        return velocityFileExists(velocityTemplateName);
      }

    } else {
      return false;
    }
  }

  private boolean isLocalVelocityTemplateUrl(String openUrl) {
    if (StringUtils.startsWith(openUrl, localhostUrl)) {
      String velocityTemplateName =
          StringUtils.replace(StringUtils.substringAfter(openUrl, localhostUrl), "html", "vm");
      return velocityFileExists(velocityTemplateName);

    } else {
      return false;
    }
  }

  private boolean velocityFileExists(String velocityTemplateName) {
    return Files.exists(Paths.get(contentDir, velocityTemplateName));
  }
}
