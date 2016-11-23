package org.sitoolkit.wt.gui.infra.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.UnExpectedException;
import org.sitoolkit.wt.gui.infra.concurrent.ExecutorContainer;

public class ConversationProcess {

    private static final Logger LOG = Logger.getLogger(ConversationProcess.class.getName());

    private Process process;

    private PrintWriter processWriter;

    ConversationProcess() {
    }

    public void start(ProcessParams params) {

        File directory = params.getDirectory();
        List<String> command = params.getCommand();

        if (process != null && process.isAlive()) {
            LOG.log(Level.WARNING, "process {0} is alive.", process);
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.environment().putAll(params.getEnviroment());

        try {
            pb.directory(directory);
            process = pb.start();
            LOG.log(Level.INFO, "process {0} starts {1}", new Object[] { process, command });

            List<StdoutListener> listeners = new ArrayList<>();
            listeners.addAll(params.getStdoutListeners());
            listeners.addAll(StdoutListenerContainer.get().getListeners());

            if (!listeners.isEmpty()) {
                ExecutorContainer.get().execute(() -> scan(process.getInputStream(), listeners));
            }

            processWriter = new PrintWriter(process.getOutputStream());

            if (!params.getExitClallbacks().isEmpty()) {
                ExecutorContainer.get().execute(() -> wait(params.getExitClallbacks()));
            }

        } catch (IOException e) {
            throw new UnExpectedException(e);
        }
    }

    private void scan(InputStream is, List<StdoutListener> listeners) {
        Scanner scanner = new Scanner(is);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            for (StdoutListener listener : listeners) {
                listener.nextLine(line);
            }
        }

        scanner.close();

    }

    private void wait(List<ProcessExitCallback> callbacks) {
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
            LOG.log(Level.INFO, "process {0} exits with code : {1}",
                    new Object[] { process, exitCode });
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "", e);
        } finally {
            for (ProcessExitCallback callback : callbacks) {
                callback.callback(exitCode);
            }
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

}
