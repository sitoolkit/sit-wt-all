package org.sitoolkit.wt.gui.domain.update;

import java.io.File;

@FunctionalInterface
public interface DownloadCallback {

    void onDownloaded(File downloadedFile);
}
