package org.sitoolkit.wt.gui.infra.maven;

import org.sitoolkit.wt.gui.infra.process.ConsoleListener;
import org.sitoolkit.wt.gui.infra.util.StrUtils;

public class MavenVersionsListener implements ConsoleListener {

    private String updatePrefix;

    private String versionPrefix;

    private boolean update = false;

    private String newVersion;

    public MavenVersionsListener() {
    }

    public MavenVersionsListener(String updatePrefix, String versionPrefix) {
        super();
        this.updatePrefix = updatePrefix;
        this.versionPrefix = versionPrefix;
    }

    @Override
    public void readLine(String line) {

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
