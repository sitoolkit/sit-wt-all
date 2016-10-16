package org.sitoolkit.wt.gui.domain;

import org.sitoolkit.wt.gui.infra.ConsoleListener;

public class JettyConsoleListener implements ConsoleListener {

    private volatile int exitLevel = -1;

    @Override
    public void readLine(String line) {
        if (line == null || line.isEmpty()) {
            return;
        }
        if ("[INFO] Started Jetty Server".equals(line)) {
            exitLevel = 0;
        } else if ("[INFO] Jetty server exiting.".equals(line)) {
            exitLevel = 1;
        } else if ("[INFO] BUILD FAILURE".equals(line)) {
            exitLevel = 2;
        }
    }

    public boolean isSuccess() {
        while (exitLevel < 0) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return exitLevel == 0;
    }
}
