package io.sitoolkit.wt.gui.domain.sample;

import java.io.IOException;
import java.nio.file.Path;
import io.sitoolkit.util.buildtoolhelper.process.ProcessExitCallback;
import io.sitoolkit.wt.domain.httpserver.SitHttpServer;

public class SampleProcessClient {

  private SitHttpServer server;

  public SampleProcessClient() {
  }

  /**
   * HTTPサーバーを起動します。
   * 
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
   * HTTPサーバーを停止します。
   *
   */
  public void stop(Path sampleDir, ProcessExitCallback callback) {
    server.stopNow();
    if (!sampleDir.toFile().exists() && callback != null) {
      callback.callback(0);
    }
  }
}
