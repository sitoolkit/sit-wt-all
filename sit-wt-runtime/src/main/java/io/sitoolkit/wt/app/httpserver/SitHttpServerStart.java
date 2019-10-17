package io.sitoolkit.wt.app.httpserver;

import java.io.IOException;
import io.sitoolkit.wt.domain.httpserver.SitHttpServer;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class SitHttpServerStart {
  private static final SitLogger LOG = SitLoggerFactory.getLogger(SitHttpServerStart.class);

  public static void main(String[] args) {
    if (args.length < 2) {
      LOG.info("httpserver.starter.appoint");
      LOG.info("httpserver.starter.usage", SitHttpServerStart.class.getName());
      System.exit(1);
    }
    int port = Integer.parseInt(args[0]);
    String baseDir = args[1];
    startServer(port, baseDir);
  }

  public static void startServer(int port, String baseDir) {
    LOG.info("httpserver.starter.start");
    try {
      SitHttpServer server = SitHttpServer.of(port, baseDir);
      server.start();
      LOG.info("httpserver.starter.end");

    } catch (IOException e) {
      LOG.error("httpserver.starter.failed");
    }
  }
}
