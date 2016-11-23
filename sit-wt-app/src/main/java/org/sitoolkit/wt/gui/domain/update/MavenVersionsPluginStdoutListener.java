package org.sitoolkit.wt.gui.domain.update;

import org.sitoolkit.wt.gui.infra.process.StdoutListener;
import org.sitoolkit.wt.gui.infra.util.StrUtils;

public class MavenVersionsPluginStdoutListener implements StdoutListener {

    private String updatePrefix;

    private String versionPrefix;

    private boolean update = false;

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

        if (update) {
            if (line.contains(versionPrefix)) {
                int idx = line.indexOf(" -> ") + 4;
                newVersion = line.substring(idx);
            }
        }
    }

    public String getNewVersion() {
        return newVersion;
    }

}
