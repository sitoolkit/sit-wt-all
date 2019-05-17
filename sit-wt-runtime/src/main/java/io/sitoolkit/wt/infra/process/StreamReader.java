package io.sitoolkit.wt.infra.process;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class StreamReader implements Runnable {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(StreamReader.class);

  private String name = "default";

  private InputStream stream;

  private String text;

  public StreamReader(String name, InputStream stream) {
    super();
    this.name = name;
    this.stream = stream;
  }

  @Override
  public void run() {
    Scanner scanner = new Scanner(stream, Charset.defaultCharset().name());
    StringBuilder sb = new StringBuilder();

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();

      sb.append(line);
      sb.append(System.lineSeparator());

      LOG.info("scanner", name, line);
    }

    text = sb.toString();
    scanner.close();
  }

  public String getText() {
    return text;
  }

}
