package io.sitoolkit.wt.domain.httpserver;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import lombok.Getter;

public class ShutdownRequestHandler implements HttpHandler {
  private static final SitLogger LOG = SitLoggerFactory.getLogger(ShutdownRequestHandler.class);

  @Getter
  private boolean requested = false;

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {

    try {
      byte[] httpResponse = "Shutdown Request Received.".getBytes("UTF-8");
      httpExchange.getResponseHeaders().add("connection", "close");
      httpExchange.sendResponseHeaders(200, httpResponse.length);
      httpExchange.getResponseBody().write(httpResponse);
      requested = true;

    } catch (Exception e) {
      LOG.warn("httpserver.internalerror", e);
      httpExchange.getResponseHeaders().add("connection", "close");
      httpExchange.sendResponseHeaders(503, 0);

    } finally {
      httpExchange.getResponseBody().close();
    }
  }
}

