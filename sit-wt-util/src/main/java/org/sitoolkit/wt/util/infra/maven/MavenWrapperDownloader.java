package org.sitoolkit.wt.util.infra.maven;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.apache.maven.wrapper.DefaultDownloader;
import org.apache.maven.wrapper.Downloader;
import org.sitoolkit.wt.util.infra.UnExpectedException;

import io.tesla.proviso.archive.UnArchiver;

public class MavenWrapperDownloader {

    private static final Logger LOG = Logger.getLogger(MavenWrapperDownloader.class.getName());

    private static String version = "0.3.0";

    private static String maven = "3.5.2";

    private static String distributionUrl;

    public static void main(String[] args) {
        download();
    }

    public static void download() {
        //
        // Fetch the latest wrapper archive
        // Unpack it in the current working project
        // Possibly interpolate the latest version of Maven in the wrapper
        // properties
        //
        File localRepository = new File(System.getProperty("user.home"), ".m2/repository");
        String artifactPath = String.format("io/takari/maven-wrapper/%s/maven-wrapper-%s.tar.gz",
                version, version);
        String wrapperUrl = String.format("https://repo1.maven.org/maven2/%s", artifactPath);
        File destination = new File(localRepository, artifactPath);
        Downloader downloader = new DefaultDownloader("mvnw", version);
        try {
            downloader.download(new URI(wrapperUrl), destination);
            UnArchiver unarchiver = UnArchiver.builder().useRoot(false).build();
            Path rootDirectory = Paths.get(".");
            unarchiver.unarchive(destination, rootDirectory.toFile());
            overwriteDistributionUrl(rootDirectory, getDistributionUrl());
            LOG.info("");
            LOG.info("The Maven Wrapper version " + version
                    + " has been successfully setup for your project.");
            LOG.info("Using Apache Maven " + maven);
            LOG.info("");
        } catch (Exception e) {
            throw new UnExpectedException(e);
        }
    }

    private static void overwriteDistributionUrl(Path rootDirectory, String distributionUrl)
            throws IOException {
        if (!isNullOrEmpty(distributionUrl)) {
            Path wrapperProperties = rootDirectory
                    .resolve(Paths.get(".mvn", "wrapper", "maven-wrapper.properties"));
            if (Files.isWritable(wrapperProperties)) {
                String distroKeyValue = "distributionUrl=" + distributionUrl;
                Files.write(wrapperProperties, distroKeyValue.getBytes(Charset.forName("UTF-8")));
            }
        }
    }

    private static String getDistributionUrl() {
        if (isNullOrEmpty(distributionUrl) && !isNullOrEmpty(maven)) {
            distributionUrl = String.format(
                    "https://repo1.maven.org/maven2/org/apache/maven/apache-maven/%s/apache-maven-%s-bin.zip",
                    maven, maven);
        }
        return distributionUrl;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

}
