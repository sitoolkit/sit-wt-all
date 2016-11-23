package org.sitoolkit.wt.gui.infra.process;

@FunctionalInterface
public interface ProcessExitCallback {

    void callback(int exitCode);
}
