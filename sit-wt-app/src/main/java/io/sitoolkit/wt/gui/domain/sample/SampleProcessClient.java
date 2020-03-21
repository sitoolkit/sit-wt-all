package io.sitoolkit.wt.gui.domain.sample;

import java.io.IOException;
import java.nio.file.Path;
import io.sitoolkit.util.buildtoolhelper.process.ProcessExitCallback;
import io.sitoolkit.wt.domain.httpserver.SitHttpServer;

public class SampleProcessClient {

  private SitHttpServer server;

  /**
   * Start HTTP server.
   *
   * @param port HTTP server port
   * @param sampleDir SIT-WT sample contents directory
   * @param callback Callback function
   */
  public void start(int port, Path sampleDir, SampleStartedCallback callback) {

    try {
      server = SitHttpServer.of(port, sampleDir.toAbsolutePath().toString());
      server.start();
      callback.onStarted(true);

    } catch (IOException e) {
      callback.onStarted(false);
    }
  }

  /**
   * Stop HTTP server.
   *
   * @param callback Callback function
   */
  public void stop(ProcessExitCallback callback) {
    if (server != null) {
      server.stopNow();
    }

    if (callback != null) {
      callback.callback(0);
    }
  }
}
