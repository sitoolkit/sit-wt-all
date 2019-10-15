package io.sitoolkit.wt.domain.httpserver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.sun.net.httpserver.HttpServer;
import io.sitoolkit.wt.app.httpserver.SitHttpServerConfig;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.thread.DaemonThreadFactory;

public class SitHttpServer {
  private static final SitLogger LOG = SitLoggerFactory.getLogger(SitHttpServer.class);

  private HttpServer server;
  private boolean running = false;

  private int port;
  private String baseDir;

  private ApplicationContext appCtx;
  private ShutdownRequestHandler shutdownRequestHandler;

  private SitHttpServer(int port, String baseDir) {
    this.port = port;
    this.baseDir = baseDir;
    this.appCtx = new AnnotationConfigApplicationContext(SitHttpServerConfig.class);
    this.shutdownRequestHandler = appCtx.getBean(ShutdownRequestHandler.class);
  }

  public static SitHttpServer of(int port, String baseDir) {
    SitHttpServer instance = new SitHttpServer(port, baseDir);
    return instance;
  }

  public void start() throws IOException {
    ExecutorService serverExecutor = Executors.newCachedThreadPool(new DaemonThreadFactory());
    serverExecutor.submit(() -> {
      try {
        startServer();
        running = true;

      } catch (IOException e) {
        LOG.error("httpserver.start.failed", e);
        throw new UncheckedIOException(e);
      }
    });

    ExecutorService shutdownRequestMonitor =
        Executors.newCachedThreadPool(new DaemonThreadFactory());
    shutdownRequestMonitor.submit(() -> {
      if (doShutdown()) {
        stopNow();
      }
      serverExecutor.shutdownNow();
    });
  }

  private void startServer() throws IOException {
    SitHttpHandler handler = appCtx.getBean(SitHttpHandler.class);
    handler.setBaseDir(baseDir);
    server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/", handler);
    server.createContext("/stop", shutdownRequestHandler);
    server.start();
  }

  public void stop(int delay) {
    if (running) {
      server.stop(delay);
      running = false;
    }
  }

  public void stopNow() {
    stop(0);
  }

  private boolean doShutdown() {
    while (!shutdownRequestHandler.isRequested() || !running) {
      try {
        TimeUnit.MILLISECONDS.sleep(10);
      } catch (InterruptedException e) {
        LOG.error("httpserver.monitoring.failed", e);
      }
    }
    LOG.info("httpserver.shutdown.requested");
    return !shutdownRequestHandler.isRequested() || !running;
  }
}
