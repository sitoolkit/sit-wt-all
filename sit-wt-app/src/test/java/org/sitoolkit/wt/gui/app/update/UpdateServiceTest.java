package org.sitoolkit.wt.gui.app.update;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.process.LogStdoutListener;
import org.sitoolkit.wt.gui.infra.process.StdoutListenerContainer;

public class UpdateServiceTest {

    UpdateService service = new UpdateService();

    private volatile boolean testing = true;

    @BeforeClass
    public static void setup() {
        MavenUtils.findAndInstall();
        StdoutListenerContainer.get().getListeners().add(new LogStdoutListener());
    }

    @Test
    public void testCheckAppUpdate() throws URISyntaxException, InterruptedException {

        File pomFile = new File(getClass().getResource("pom.xml").toURI());

        service.checkSitWtAppUpdate(pomFile, newVersion -> {
            assertThat("newVersion", newVersion, is("1.2"));
            testing = false;
        });

        while (testing) {
        }
    }

    @Test
    public void testDownload() {

        service.downloadSitWtApp(new File("target"), "1.2", downloadedFile -> {
            downloadedFile.deleteOnExit();
            assertThat("file downloaded", downloadedFile.exists(), is(true));
            testing = false;
        });

        while (testing) {
        }
    }

}
