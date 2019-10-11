package io.sitoolkit.wt.app.httpserver;

import java.io.IOException;
import io.sitoolkit.wt.domain.httpserver.SitHttpServer;

public class SitHttpServerStart {

  public static void main(String[] args) throws IOException {

    if (args.length < 2) {
      System.exit(1);
    }
    int port = Integer.parseInt(args[0]);
    String baseDir = args[1];

    SitHttpServer server = SitHttpServer.of(port, baseDir);
    server.start();
  }
}
