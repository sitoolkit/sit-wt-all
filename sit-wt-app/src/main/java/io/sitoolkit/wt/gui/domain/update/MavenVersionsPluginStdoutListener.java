package io.sitoolkit.wt.gui.domain.update;

import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;
import io.sitoolkit.wt.util.infra.util.StrUtils;

public class MavenVersionsPluginStdoutListener implements StdoutListener {

    private String updatePrefix;

    private String versionPrefix;

    private boolean update = false;

    private String currentVersion;

    private String newVersion;

    public MavenVersionsPluginStdoutListener() {
        // TODO Auto-generated constructor stub
    }

    public MavenVersionsPluginStdoutListener(String updatePrefix, String versionPrefix) {
        super();
        this.updatePrefix = updatePrefix;
        this.versionPrefix = versionPrefix;
    }

    @Override
    public void nextLine(String line) {

        if (StrUtils.equals(updatePrefix, line)) {
            update = true;
        }

        if (line.contains(versionPrefix)) {
            if (update) {
                int idx = line.indexOf(" -> ");
                currentVersion = line.substring(line.indexOf("... ") + 4, idx);
                newVersion = line.substring(idx + 4);
            } else {
                currentVersion = line.substring(line.indexOf("... ") + 4);
            }
        }
    }

    public String getNewVersion() {
        return newVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

}
