package org.sitoolkit.wt.util.infra.process;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.util.infra.UnExpectedException;
import org.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;

public class ConversationProcess {

    private static final Logger LOG = Logger.getLogger(ConversationProcess.class.getName());

    private static final LogStdoutListener LOG_STDOUT_LISTENER = new LogStdoutListener(LOG,
            Level.INFO, "stdout");
    private static final LogStdoutListener LOG_STDERR_LISTENER = new LogStdoutListener(LOG,
            Level.SEVERE, "stderr");

    private Process process;

    private PrintWriter processWriter;

    private StringBuilderStdoutListener defaultStderrListener = new StringBuilderStdoutListener();

    ConversationProcess() {
    }

    public void startWithProcessWait(ProcessParams params) {
        if (params.getExitClallbacks().isEmpty()) {
            params.getExitClallbacks().add(new NopProcessExitCallback());
        }
        params.setProcessWait(true);
        start(params);
    }

    public void start(ProcessParams params) {

        File directory = params.getDirectory();
        if (directory == null) {
            directory = ProcessParams.getDefaultCurrentDir();
        }
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

            List<StdoutListener> stdoutListeners = new ArrayList<>();
            stdoutListeners.add(LOG_STDOUT_LISTENER);
            stdoutListeners.addAll(params.getStdoutListeners());
            stdoutListeners.addAll(StdoutListenerContainer.get().getListeners());

            List<StdoutListener> stderrListeners = new ArrayList<>();
            stderrListeners.add(LOG_STDERR_LISTENER);

            if (params.isProcessWait()) {
                scan(process.getInputStream(), stdoutListeners);
                scan(process.getErrorStream(), stderrListeners);
            } else {
                ExecutorContainer.get().execute(() -> scan(process.getInputStream(), stdoutListeners));
                ExecutorContainer.get().execute(() -> scan(process.getErrorStream(), stderrListeners));
            }

            processWriter = new PrintWriter(process.getOutputStream());

            if (params.isProcessWait()) {
                wait(params.getExitClallbacks());
            } else {
                ExecutorContainer.get().execute(() -> wait(params.getExitClallbacks()));
            }

        } catch (Exception e) {
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

            if (exitCode != 0) {
                LOG.log(Level.SEVERE, "{0} {1}",
                        new Object[] { System.lineSeparator(), defaultStderrListener });
            }

            if (callbacks != null) {
                for (ProcessExitCallback callback : callbacks) {
                    callback.callback(exitCode);
                }
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
