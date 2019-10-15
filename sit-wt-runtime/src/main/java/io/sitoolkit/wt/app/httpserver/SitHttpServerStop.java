package io.sitoolkit.wt.app.httpserver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class SitHttpServerStop {
  private static final SitLogger LOG = SitLoggerFactory.getLogger(SitHttpServerStop.class);

  public static void main(String[] args) {

    LOG.info("httpserver.stopper.start");
    try {
      if (args.length < 1) {
        LOG.info("httpserver.stopper.appoint");
        LOG.info("httpserver.stopper.usage", SitHttpServerStop.class.getName());
        System.exit(1);
      }
      URL url = new URL(createStopUrl(Integer.parseInt(args[0])));

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setInstanceFollowRedirects(false);
      connection.connect();
      connection.getInputStream().close();
      LOG.info("httpserver.stopper.end");

    } catch (IOException e) {
      LOG.error("httpserver.stopper.failed", e);
    }
  }

  private static String createStopUrl(int port) {
    return "http://localhost:" + port + "/stop";
  }
}
