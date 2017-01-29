package org.sitoolkit.wt.gui.app.update;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.testutil.ThreadUtils;

public class UpdateServiceTest {

    UpdateService service = new UpdateService();

    private volatile boolean tested = false;

    @BeforeClass
    public static void setup() {
        MavenUtils.findAndInstall();
        MavenUtils.downloadRepository();
    }

    @Test
    public void testCheckAppUpdate() throws URISyntaxException, InterruptedException {

        File pomFile = new File(getClass().getResource("pom.xml").toURI());

        service.checkSitWtAppUpdate(pomFile, newVersion -> {
            // TODO newVersionの値をpomから取得
            assertThat("newVersion", newVersion, is("2.1"));
            tested = true;
        });

        ThreadUtils.waitFor(() -> tested);
    }

    @Test
    public void testDownload() {

        service.downloadSitWtApp(new File("target"), "2.0", downloadedFile -> {
            downloadedFile.deleteOnExit();
            assertThat("file downloaded", downloadedFile.exists(), is(true));
            tested = true;
        });

        ThreadUtils.waitFor(() -> tested);
    }

}
