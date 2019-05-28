package io.sitoolkit.wt.infra.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.sitoolkit.wt.app.config.RuntimeConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuntimeConfig.class)
public class WebDriverInstallerTest {

  @Test
  public void test() {

    WebDriverInstaller installer = WebDriverInstaller.getInstance();

    File repositoryDir = new File(installer.getRrepositoryDir(""));
    FileUtils.deleteQuietly(repositoryDir);

    File ieDriver = new File(installer.installIeDriver());
    File chromeDriver = new File(installer.installChromeDriver());

    assertThat("iedriverのインストール失敗", ieDriver.exists(), is(true));
    assertThat("chromedriverのインストール失敗", chromeDriver.exists(), is(true));

    boolean isWindows10 = "Windows 10".equals(System.getProperty("os.name"));
    if (isWindows10) {
      File edgeDriver = new File(installer.installEdgeDriver());
      assertThat("edgedriverのインストール失敗", edgeDriver.exists(), is(true));
    }
  }

}
