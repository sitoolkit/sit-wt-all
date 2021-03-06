package io.sitoolkit.wt.gui.domain.sample;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class JettyMavenPluginStdoutListener implements StdoutListener {

  private static final SitLogger LOG =
      SitLoggerFactory.getLogger(JettyMavenPluginStdoutListener.class);

  private volatile int exitLevel = -1;

  public JettyMavenPluginStdoutListener() {}

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
