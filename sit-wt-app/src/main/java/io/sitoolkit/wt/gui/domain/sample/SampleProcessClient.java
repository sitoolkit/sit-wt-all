package io.sitoolkit.wt.gui.domain.sample;

import java.io.IOException;
import java.nio.file.Path;
import io.sitoolkit.util.buildtoolhelper.process.ProcessExitCallback;
import io.sitoolkit.wt.domain.httpserver.SitHttpServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleProcessClient {

  private SitHttpServer server;

  public SampleProcessClient() {
  }

  /**
   * HTTPサーバーを起動します。
   * 
   */
  public void start(Path basetDir, Path sampleDir, SampleStartedCallback callback) {

    try {

      server = SitHttpServer.of(8280, sampleDir.toAbsolutePath().toString());
      log.info("httpserver start : server={}", server);
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
  public void stop(Path baseDir, Path sampleDir, ProcessExitCallback callback) {
    log.info("httpserver stop : server={}", server);
    server.stopNow();
    if (!sampleDir.toFile().exists() && callback != null) {
      callback.callback(0);
    }
  }
}
