package org.sitoolkit.wt.gui.infra.maven;

@FunctionalInterface
public interface UpdateCallback {
    void callback(String newVersion);
}