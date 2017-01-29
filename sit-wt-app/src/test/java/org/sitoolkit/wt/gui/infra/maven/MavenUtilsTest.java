package org.sitoolkit.wt.gui.infra.maven;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.junit.Test;
import org.sitoolkit.wt.gui.infra.util.FileIOUtils;

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

}
