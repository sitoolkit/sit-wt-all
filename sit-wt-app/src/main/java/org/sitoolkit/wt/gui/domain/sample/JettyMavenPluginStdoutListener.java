package org.sitoolkit.wt.gui.domain.sample;

import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.sitoolkit.wt.util.infra.process.StdoutListener;

public class JettyMavenPluginStdoutListener implements StdoutListener {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(JettyMavenPluginStdoutListener.class);

    private volatile int exitLevel = -1;

    public JettyMavenPluginStdoutListener() {
    }

    @Override
    public void nextLine(String line) {
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
                LOG.warn("app.noMsg", e);
            }
        }
        return exitLevel == 0;
    }

}
