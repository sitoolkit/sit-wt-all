package io.sitoolkit.wt.util.infra.maven;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.junit.Test;

import io.sitoolkit.wt.util.infra.maven.MavenUtils;
import io.sitoolkit.wt.util.infra.proxysetting.ProxySetting;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;

public class MavenUtilsTest {

    @Test
    public void testSetSitWtVersion() {
        File pomFile = new File(getClass().getResource("maven-utils-test-pom.xml").getPath());
        File destPomFile = new File("target/maven-utils-test-pom.xml");
        if (destPomFile.exists()) {
            destPomFile.delete();
        }

        int result = MavenUtils.setSitWtVersion(pomFile, "0.0.1", destPomFile);

        assertThat("same version", result, is(1));

        result = MavenUtils.setSitWtVersion(pomFile, "0.0.2", destPomFile);

        assertThat("success to set version", result, is(0));

        String[] destPomLines = FileIOUtils.file2str(destPomFile).split(System.lineSeparator());
        assertThat(destPomLines[28].trim(), is("<sitwt.version>0.0.2</sitwt.version>"));
    }

    @Test
    public void testReadProxySetting() {
        File settingsXmlForTest = new File(getClass().getResource("settings-test.xml").getPath());

        ProxySetting proxySetting = MavenUtils.readProxySetting(settingsXmlForTest);
        assertThat(proxySetting.getProxyActive(), is("true"));
        assertThat(proxySetting.getProxyHost(), is("127.0.0.2"));
        assertThat(proxySetting.getProxyPort(), is("8082"));
        assertThat(proxySetting.getNonProxyHosts(), is("192.168.2.*|localhost"));
    }

}
