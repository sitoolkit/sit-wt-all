package org.sitoolkit.wt.infra;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessUtils.class);

    public static void exec(String... command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            Process process = pb.start();
            process.waitFor(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn("", e);
        }
    }
}
