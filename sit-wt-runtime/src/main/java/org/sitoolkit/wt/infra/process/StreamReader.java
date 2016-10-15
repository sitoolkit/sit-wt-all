package org.sitoolkit.wt.infra.process;

import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamReader implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(StreamReader.class);

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
        Scanner scanner = new Scanner(stream);
        StringBuilder sb = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            sb.append(line);
            sb.append(System.lineSeparator());

            LOG.info("[{}] {}", name, line);
        }

        text = sb.toString();
        scanner.close();
    }

    public String getText() {
        return text;
    }

}