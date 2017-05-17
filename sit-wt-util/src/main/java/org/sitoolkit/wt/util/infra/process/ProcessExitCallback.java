package org.sitoolkit.wt.util.infra.process;

@FunctionalInterface
public interface ProcessExitCallback {

    void callback(int exitCode);
}
