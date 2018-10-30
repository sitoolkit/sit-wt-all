package io.sitoolkit.wt.gui.app.update;

import java.io.File;

import io.sitoolkit.wt.gui.domain.update.DownloadCallback;
import io.sitoolkit.wt.gui.domain.update.UpdateProcessClient;
import io.sitoolkit.wt.gui.domain.update.VersionCheckMode;
import io.sitoolkit.wt.gui.domain.update.VersionCheckedCallback;

public class UpdateService {

    UpdateProcessClient client = new UpdateProcessClient();

    public UpdateService() {
    }

    public void checkSitWtAppUpdate(File pomFile, VersionCheckedCallback callback) {

        VersionCheckMode mode = VersionCheckMode.PARENT;
        client.checkVersion(pomFile, mode, callback);
    }

    public void downloadSitWtApp(File destDir, String version, DownloadCallback callback) {

        client.dependencyCopy(destDir, "io.sitoolkit.wt:sit-wt-app:" + version);
        callback.onDownloaded(new File(destDir, "sit-wt-app-" + version + ".jar"));
    }
}
