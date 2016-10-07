package org.sitoolkit.wt.gui.infra;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ConversationProcess {

    private static final Logger LOG = Logger.getLogger(ConversationProcess.class.getName());

    private Process process;

    private PrintWriter processWriter;

    public void start(Console console, File directory, String... command) {
        start(console, directory, Arrays.asList(command));
    }

    public void start(Console console, File directory, List<String> command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            pb.directory(directory);
            process = pb.start();
            LOG.info("process " + process + " starts " + command);

            ExecutorContainer.get()
                    .execute(new ConsoleStreamReader(process.getInputStream(), console));

            processWriter = new PrintWriter(process.getOutputStream());
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }
    }

    public void input(String input) {
        processWriter.println(input);
        processWriter.flush();
    }

    public void destroy() {
        if (process.isAlive()) {
            process.destroy();
        }
    }

    public void waitFor(WaitCallback callback) {
        if (process != null) {
            ExecutorContainer.get().execute(() -> {
                try {
                    int exitCode = process.waitFor();
                    LOG.info("process " + process + " exits with code : " + exitCode);
                } catch (InterruptedException e) {
                    // TODO 例外処理
                    e.printStackTrace();
                } finally {
                    callback.callback();
                }
            });
        }
    }

    public static interface WaitCallback {
        void callback();
    }

}
