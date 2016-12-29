package org.sitoolkit.wt.gui.app.update;

import java.io.File;

import org.sitoolkit.wt.gui.domain.update.DownloadCallback;
import org.sitoolkit.wt.gui.domain.update.MavenVersionsPluginStdoutListener;
import org.sitoolkit.wt.gui.domain.update.UpdateProcessClient;
import org.sitoolkit.wt.gui.domain.update.VersionCheckMode;
import org.sitoolkit.wt.gui.domain.update.VersionCheckedCallback;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class UpdateService {

    UpdateProcessClient client = new UpdateProcessClient();

    public UpdateService() {
    }

    public void checkSitWtAppUpdate(File pomFile, VersionCheckedCallback callback) {

        ProcessParams params = new ProcessParams();

        VersionCheckMode mode = VersionCheckMode.PARENT;

        MavenVersionsPluginStdoutListener listener = new MavenVersionsPluginStdoutListener(
                mode.getUpdateLine(), "org.sitoolkit.wt:sit-wt-all ..");

        params.getStdoutListeners().add(listener);

        params.getExitClallbacks().add(exitCode -> {
            callback.onCallback(listener.getNewVersion());
        });

        client.checkVersion(pomFile, mode, params);
    }

    public void downloadSitWtApp(File destDir, String version, DownloadCallback callback) {

        ProcessParams params = new ProcessParams();

        params.getExitClallbacks().add(exitCode -> callback
                .onDownloaded(new File(destDir, "sit-wt-app-" + version + ".jar")));

        client.dependencyCopy(destDir, "org.sitoolkit.wt:sit-wt-app:" + version, params);
    }
}
