package io.sitoolkit.wt.util.infra.maven;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import io.sitoolkit.wt.util.infra.maven.MavenWrapperDownloader;

public class MavenWrapperDownloaderTest {

    private List<Path> pathes = Arrays.asList(Paths.get(".mvn/wrapper/maven-wrapper.jar"),
            Paths.get(".mvn/wrapper/maven-wrapper.properties"), Paths.get("mvnw"),
            Paths.get("mvnw.cmd"), Paths.get(".mvn/wrapper"), Paths.get(".mvn"));

    @Test
    public void test() {

        MavenWrapperDownloader.download(Paths.get("."));

        pathes.forEach(
                path -> assertThat(path + "doesnt'e xists", path.toFile().exists(), is(true)));

    }

    @After
    public void tearDown() {
        pathes.forEach(path -> path.toFile().delete());
    }
}
