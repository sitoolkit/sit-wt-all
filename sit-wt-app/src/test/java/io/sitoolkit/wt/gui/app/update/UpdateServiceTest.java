package io.sitoolkit.wt.gui.app.update;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import io.sitoolkit.util.buildtoolhelper.maven.MavenUtils;
import io.sitoolkit.wt.gui.infra.util.VersionUtils;
import io.sitoolkit.wt.gui.testutil.ThreadUtils;

public class UpdateServiceTest {

  UpdateService service = new UpdateService(".");

  private volatile boolean tested = false;

  @BeforeClass
  public static void setup() {
    MavenUtils.findAndInstall();
    // MavenUtils.downloadRepository();
  }

  @Test
  public void testCheckAppUpdate()
      throws URISyntaxException, InterruptedException, ParserConfigurationException, SAXException,
          IOException, XPathExpressionException {

    String pom = "pom.xml";

    File pomFile = new File(getClass().getResource(pom).toURI());
    String currentVersion = getVersion(pom);

    service.checkSitWtAppUpdate(
        pomFile,
        newVersion -> {
          assertThat("newVersion", VersionUtils.isNewer(currentVersion, newVersion), is(true));
          tested = true;
        });

    ThreadUtils.waitFor(() -> tested);
  }

  @Test
  public void testDownload() {

    service.downloadSitWtApp(
        new File("target"),
        "3.0.0-alpha.1",
        downloadedFile -> {
          downloadedFile.deleteOnExit();
          assertThat("file downloaded", downloadedFile.exists(), is(true));
          tested = true;
        });

    ThreadUtils.waitFor(() -> tested);
  }

  private String getVersion(String pomFile)
      throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

    try (InputStream inputStream = getClass().getResourceAsStream(pomFile)) {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(inputStream);

      XPathFactory xpfactory = XPathFactory.newInstance();
      XPath xpath = xpfactory.newXPath();
      return xpath.evaluate("/project/parent/version", document);
    }
  }
}
