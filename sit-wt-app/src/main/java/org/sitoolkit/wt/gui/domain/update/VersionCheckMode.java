package org.sitoolkit.wt.gui.domain.update;

public enum VersionCheckMode {
    PARENT("versions:display-parent-updates",
            "[INFO] The parent project has a newer version:"), PROPERTY(
                    "versions:display-property-updates",
                    "[INFO] The following version property updates are available:");

    private String updateLine;
    private String pluginGoal;

    private VersionCheckMode(String pluginGoal, String updateLine) {
        this.updateLine = updateLine;
        this.pluginGoal = pluginGoal;
    }

    public String getUpdateLine() {
        return updateLine;
    }

    public String getPluginGoal() {
        return pluginGoal;
    }

}
