package org.sitoolkit.wt.infra.selenium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.sitoolkit.wt.infra.ConfigurationException;
import org.sitoolkit.wt.infra.ProcessUtils;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverInstaller {

    private static Logger LOG = LoggerFactory.getLogger(WebDriverInstaller.class);

    private WebDriverBinaryInfo winChromeBinaryInfo = new WebDriverBinaryInfo("win", "chrome");
    private WebDriverBinaryInfo macChromeBinaryInfo = new WebDriverBinaryInfo("mac", "chrome");
    private WebDriverBinaryInfo ieBinaryInfo = new WebDriverBinaryInfo("ie");
    private WebDriverBinaryInfo edgeBinaryInfo = new WebDriverBinaryInfo("edge");
    private WebDriverBinaryInfo safariBinaryInfo = new WebDriverBinaryInfo("safari");

    class WebDriverBinaryInfo {

        public WebDriverBinaryInfo(String driver) {
            super();
            this.driver = driver;
            sysPropKey = "webdriver." + driver + ".driver";
            downloadDir = getDownloadDir(driver);
            installDir = getInstallDir(driver);
        }

        public WebDriverBinaryInfo(String os, String driver) {
            this(driver);
            this.os = os;
        }

        String os;
        String driver;
        String sysPropKey;
        String downloadUrl;
        String downloadDir;
        String zipEntry;
        String installDir;
        String installFile;
    }

    @PostConstruct
    public void init() {
        Map<String, String> prop = PropertyUtils.loadAsMap("/webdriver-default.properties", false);
        prop.putAll(PropertyUtils.loadAsMap("/webdriver.properties", true));

        setProperties(prop, winChromeBinaryInfo);
        setProperties(prop, macChromeBinaryInfo);
        setProperties(prop, ieBinaryInfo);
        setProperties(prop, edgeBinaryInfo);
        setProperties(prop, safariBinaryInfo);
    }

    private void setProperties(Map<String, String> prop, WebDriverBinaryInfo binaryInfo) {
        String os = binaryInfo.os == null ? "" : binaryInfo.os + ".";

        binaryInfo.downloadUrl = prop.get(os + binaryInfo.driver + ".downloadUrl");
        binaryInfo.zipEntry = prop.get(os + binaryInfo.driver + ".zipEntry");
        binaryInfo.installFile = prop.get(os + binaryInfo.driver + ".installFile");

        String installDir = prop.get(os + binaryInfo.driver + ".installDir");
        if (StringUtils.isNotEmpty(installDir)) {
            binaryInfo.installDir = installDir;
        }
    }

    public String installEdgeDriver() {
        return install(edgeBinaryInfo);
    }

    public String installChromeDriver() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return install(winChromeBinaryInfo);
        } else {
            return install(macChromeBinaryInfo);
        }
    }

    public String installIeDriver() {
        return install(ieBinaryInfo);
    }

    public void installSafariDriver() {
        File installFile = new File(safariBinaryInfo.installDir, safariBinaryInfo.installFile);

        try {
            if (!installFile.exists()) {
                URL downloadUrl = new URL(safariBinaryInfo.downloadUrl);
                LOG.info("Safari Driverをダウンロードします {} -> {}", downloadUrl,
                        installFile.getAbsolutePath());
                FileUtils.copyURLToFile(downloadUrl, installFile);
            }

            LOG.info("Safari Driverをインストールします {}", installFile.getAbsolutePath());

            String script = IOUtils
                    .toString(ClassLoader.getSystemResource("install-safaridriver.scpt"), "UTF-8");
            ProcessUtils.exec("osascript", "-e", script, installFile.getAbsolutePath());

            JOptionPane.showMessageDialog(null,
                    "Safariで機能拡張\"WebDriver\"をインストールしたらOKボタンをクリックしてください。");

            script = IOUtils.toString(ClassLoader.getSystemResource("quit-safari.scpt"), "UTF-8");
            ProcessUtils.exec("osascript", "-e", script, installFile.getAbsolutePath());

        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    protected File findInstallFile(WebDriverBinaryInfo binaryInfo) {
        String installPath = System.getProperty(binaryInfo.sysPropKey);
        File installFile = null;

        if (StringUtils.isNotEmpty(installPath)) {
            installFile = new File(installPath);
        }

        if (installFile != null && installFile.exists()) {
            return installFile;
        }

        installFile = new File(binaryInfo.installDir, binaryInfo.installFile);

        return installFile;
    }

    protected String install(WebDriverBinaryInfo binaryInfo) {
        File installFile = findInstallFile(binaryInfo);
        System.setProperty(binaryInfo.sysPropKey, installFile.getAbsolutePath());

        if (installFile.exists()) {
            LOG.info("{}はインストール済みです {}", binaryInfo.sysPropKey, installFile.getAbsolutePath());
            return installFile.getAbsolutePath();
        }

        LOG.info("{}をインストールします", binaryInfo.sysPropKey);

        try {
            URL downloadUrl = new URL(binaryInfo.downloadUrl);
            File downloadFile = new File(binaryInfo.downloadDir, downloadUrl.getFile());

            if (downloadFile.exists()) {
                LOG.info("{}はダウンロード済みです {}", binaryInfo.sysPropKey, downloadFile.getAbsolutePath());
            } else {
                LOG.info("{}をダウンロードします {} -> {}", new Object[] { binaryInfo.sysPropKey, downloadUrl,
                        downloadFile.getAbsolutePath() });
                FileUtils.copyURLToFile(downloadUrl, downloadFile);
            }

            // Windowsインストーラ―の場合は無人モードで実行
            if (downloadFile.getName().endsWith(".msi")) {
                LOG.info("{}をインストーラを実行します {}",
                        new Object[] { binaryInfo.sysPropKey, downloadFile.getAbsolutePath() });
                ProcessUtils.exec("msiexec", "/i", downloadFile.getAbsolutePath(), "/passive");

            } else if (StringUtils.isEmpty(binaryInfo.zipEntry)) {
                LOG.info("{}を配置します {} -> {} ", new Object[] { binaryInfo.sysPropKey,
                        downloadFile.getAbsolutePath(), installFile.getAbsolutePath() });
                FileUtils.copyFile(downloadFile, installFile);

            } else {
                LOG.info("{}を展開します {} -> {} ", new Object[] { binaryInfo.sysPropKey,
                        downloadFile.getAbsolutePath(), installFile.getAbsolutePath() });
                extractOne(downloadFile, binaryInfo.zipEntry, installFile);
            }

            if (!SystemUtils.IS_OS_WINDOWS) {
                ProcessUtils.exec("chmod", "u+x", installFile.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }

        return installFile.getAbsolutePath();
    }

    private void extractOne(File srcFile, String entryName, File dstFile) throws IOException {

        try (ZipFile zipFile = new ZipFile(srcFile)) {
            Enumeration<? extends ZipEntry> enu = zipFile.entries();

            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = enu.nextElement();

                if (zipEntry.getName().equals(entryName)) {

                    File parentDir = dstFile.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    try (InputStream is = zipFile.getInputStream(zipEntry)) {
                        Files.copy(is, dstFile.toPath());
                    }
                }

            }
        }
    }

    private String getDownloadDir(String driver) {
        return getRrepositoryDir(driver) + File.separatorChar + "download";
    }

    private String getInstallDir(String driver) {
        return getRrepositoryDir(driver) + File.separatorChar + "runtime";
    }

    String getRrepositoryDir(String driver) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "C:\\ProgramData\\sitoolkit\\repository\\selenium\\" + driver;
        } else {
            return System.getProperty("user.home") + "/.sitoolkit/repository/selenium/" + driver;
        }
    }

}
