package org.sitoolkit.wt.infra.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class WebDriverInstallerTest {

    @Test
    public void test() {

        WebDriverInstaller installer = new WebDriverInstaller();

        installer.init();

        File repositoryDir = new File(installer.getRrepositoryDir(""));
        FileUtils.deleteQuietly(repositoryDir);

        File edgeDriver = new File(installer.installEdgeDriver());
        // File ieDriver = new File(installer.installIeDriver());
        // File chromeDriver = new File(installer.installChromeDriver());

        assertThat("edgedriverのインストール失敗", edgeDriver.exists(), is(true));
        // assertThat("iedriverのインストール失敗", ieDriver.exists(), is(true));
        // assertThat("chromedriverのインストール失敗", chromeDriver.exists(), is(true));
    }

}
