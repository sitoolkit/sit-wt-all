package io.sitoolkit.wt.domain.httpserver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.sitoolkit.wt.app.httpserver.SitHttpServerConfig;
import io.sitoolkit.wt.infra.thread.DaemonThreadFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitHttpServer {

  private ApplicationContext appCtx;

  private HttpServer server;
  private int port;
  private String baseDir;

  private HttpServer shutdownListener;
  private ShutdownRequestHandler shutdownRequestHandler;
  private final int shutdownPort = 9999;

  private SitHttpServer(int port, String baseDir) {
    this.port = port;
    this.baseDir = baseDir;
    this.appCtx = new AnnotationConfigApplicationContext(SitHttpServerConfig.class);
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

      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });

    ExecutorService shutdownRequestMonitor =
        Executors.newCachedThreadPool(new DaemonThreadFactory());
    shutdownRequestMonitor.submit(() -> {
      if (getShutdownRequest()) {
        stopNow();
      }
      serverExecutor.shutdownNow();
    });
  }

  private void startServer() throws IOException {
    SitHttpHandler handler = appCtx.getBean(SitHttpHandler.class);
    handler.setBaseDir(baseDir);
    server = startServer(port, handler);

    shutdownRequestHandler = appCtx.getBean(ShutdownRequestHandler.class);
    shutdownListener = startServer(shutdownPort, shutdownRequestHandler);
    log.info("start server={}, shutdownListener={}", server, shutdownListener);
  }

  private HttpServer startServer(int port, HttpHandler handler) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/", handler);
    server.start();
    return server;
  }

  public void stop(int delay) {
    log.info("stop server={}, shutdownListener={}", server, shutdownListener);
    server.stop(delay);
    shutdownListener.stop(delay);
  }

  public void stopNow() {
    stop(0);
  }

  private boolean getShutdownRequest() {
    while (!shutdownRequestHandler.isRequested()) {
      try {
        TimeUnit.MILLISECONDS.sleep(10);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return shutdownRequestHandler.isRequested();
  }
}
