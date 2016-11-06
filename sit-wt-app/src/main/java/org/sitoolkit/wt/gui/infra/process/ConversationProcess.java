package org.sitoolkit.wt.gui.infra.process;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.UnExpectedException;
import org.sitoolkit.wt.gui.infra.concurrent.ExecutorContainer;

public class ConversationProcess {

    private static final Logger LOG = Logger.getLogger(ConversationProcess.class.getName());

    private Process process;

    private PrintWriter processWriter;

    public void start(Console console, File directory, String... command) {
        start(console, directory, Arrays.asList(command));
    }

    public void start(Console console, File directory, List<String> command) {

        if (directory == null || !directory.exists()) {
            LOG.log(Level.WARNING, "cannot start command {0} because directory {1} doesn't exist",
                    new Object[] { command, directory });

            return;
        }

        if (process != null && process.isAlive()) {
            LOG.log(Level.WARNING, "process {0} is alive.", process);
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            pb.directory(directory);
            process = pb.start();
            LOG.log(Level.INFO, "process {0} starts {1}", new Object[] { process, command });

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
        if (process != null && process.isAlive()) {
            process.destroy();
        }
    }

    public void onExit(OnExitCallback callback) {
        if (process != null) {
            ExecutorContainer.get().execute(() -> {
                int exitCode = 0;
                try {
                    exitCode = process.waitFor();
                    LOG.log(Level.INFO, "process {0} exits with code : {1}",
                            new Object[] { process, exitCode });
                } catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "", e);
                } finally {
                    callback.callback(exitCode);
                }
            });
        }
    }

    public static interface OnExitCallback {
        void callback(int exitCode);
    }

}
