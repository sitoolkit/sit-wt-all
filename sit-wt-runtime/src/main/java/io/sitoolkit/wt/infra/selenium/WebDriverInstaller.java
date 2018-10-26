package io.sitoolkit.wt.infra.selenium;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import io.sitoolkit.util.buidtoolhelper.proxysetting.ProxySettingService;
import io.sitoolkit.wt.infra.ConfigurationException;
import io.sitoolkit.wt.infra.PropertyUtils;
import io.sitoolkit.wt.infra.SitRepository;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.process.ProcessUtils;

public class WebDriverInstaller {

    private static SitLogger LOG = SitLoggerFactory.getLogger(WebDriverInstaller.class);

    private WebDriverBinaryInfo winGeckoBinaryInfo = new WebDriverBinaryInfo("win", "gecko");
    private WebDriverBinaryInfo macGeckoBinaryInfo = new WebDriverBinaryInfo("mac", "gecko");
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

        setProperties(prop, winGeckoBinaryInfo);
        setProperties(prop, macGeckoBinaryInfo);
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

    public String installGeckoDriver() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return install(winGeckoBinaryInfo);
        } else if (SystemUtils.IS_OS_MAC) {
            return install(macGeckoBinaryInfo);
        } else {
            return "";
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
                ProxySettingService.getInstance().loadProxy();

                URL downloadUrl = new URL(safariBinaryInfo.downloadUrl);
                LOG.info("safari.dawnload", downloadUrl, installFile.getAbsolutePath());
                FileUtils.copyURLToFile(downloadUrl, installFile);
            }

            LOG.info("safari.install", installFile.getAbsolutePath());

            String script = IOUtils
                    .toString(ClassLoader.getSystemResource("install-safaridriver.scpt"), "UTF-8");
            ProcessUtils.exec("osascript", "-e", script, installFile.getAbsolutePath());

            JOptionPane.showMessageDialog(null,
                    "Safariで機能拡張\"WebDriver\"をインストールしたらOKボタンをクリックしてください。");

            script = IOUtils.toString(ClassLoader.getSystemResource("quit-safari.scpt"), "UTF-8");
            ProcessUtils.exec("osascript", "-e", script, installFile.getAbsolutePath());

        } catch (IOException e) {
            throw new ConfigurationException(e);
        } catch (Exception exp) {
            throw new ConfigurationException(exp);
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
            LOG.info("install.exists", binaryInfo.sysPropKey, installFile.getAbsolutePath());
            return installFile.getAbsolutePath();
        }

        LOG.info("install", binaryInfo.sysPropKey);

        try {
            URL downloadUrl = new URL(binaryInfo.downloadUrl);
            File downloadFile = new File(binaryInfo.downloadDir, downloadUrl.getFile());

            if (downloadFile.exists()) {
                LOG.info("download.exists", binaryInfo.sysPropKey, downloadFile.getAbsolutePath());
            } else {
                ProxySettingService.getInstance().loadProxy();

                LOG.info("download2", new Object[] { binaryInfo.sysPropKey, downloadUrl,
                        downloadFile.getAbsolutePath() });
                FileUtils.copyURLToFile(downloadUrl, downloadFile);
            }

            // Windowsインストーラ―の場合は無人モードで実行
            if (downloadFile.getName().endsWith(".msi")) {
                LOG.info("installer.execute",
                        new Object[] { binaryInfo.sysPropKey, downloadFile.getAbsolutePath() });
                ProcessUtils.exec("msiexec", "/i", downloadFile.getAbsolutePath(), "/passive");

            } else if (StringUtils.isEmpty(binaryInfo.zipEntry)) {
                LOG.info("put", new Object[] { binaryInfo.sysPropKey,
                        downloadFile.getAbsolutePath(), installFile.getAbsolutePath() });
                FileUtils.copyFile(downloadFile, installFile);

            } else {
                LOG.info("open", new Object[] { binaryInfo.sysPropKey,
                        downloadFile.getAbsolutePath(), installFile.getAbsolutePath() });
                extractOne(downloadFile, binaryInfo.zipEntry, installFile);
            }

            if (!SystemUtils.IS_OS_WINDOWS) {
                ProcessUtils.exec("chmod", "u+x", installFile.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } catch (Exception exp) {
            throw new ConfigurationException(exp);
        }

        return installFile.getAbsolutePath();
    }

    private void extractOne(File srcFile, String entryName, File dstFile) throws IOException {
        if (srcFile.getName().endsWith(".zip")) {
            extractOneFromZip(srcFile, entryName, dstFile);
        } else if (srcFile.getName().endsWith(".tar.gz")) {
            extractOneFromTarGz(srcFile, entryName, dstFile);
        }
    }

    private void extractOneFromZip(File srcFile, String entryName, File dstFile)
            throws IOException {

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

    private void extractOneFromTarGz(File srcFile, String entryName, File dstFile)
            throws IOException {
        TarArchiveInputStream tarInput = new TarArchiveInputStream(
                new GzipCompressorInputStream(new FileInputStream(srcFile)));
        TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
        while (currentEntry != null) {

            if (!currentEntry.getName().equals(entryName)) {
                continue;
            }

            byte[] content = new byte[(int) currentEntry.getSize()];
            IOUtils.read(tarInput, content);
            FileUtils.writeByteArrayToFile(dstFile, content);

            currentEntry = tarInput.getNextTarEntry();
        }
    }

    private String getDownloadDir(String driver) {
        return getRrepositoryDir(driver) + File.separatorChar + "download";
    }

    private String getInstallDir(String driver) {
        return getRrepositoryDir(driver) + File.separatorChar + "runtime";
    }

    String getRrepositoryDir(String driver) {
        return SitRepository.getRepositoryPath() + File.separator + "selenium" + File.separator
                + driver;
    }

}
