package org.sitoolkit.wt.gui.infra;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class ConversationProcess {

    private Process process;

    private PrintWriter processWriter;

    private boolean running;

    public void start(Console console, File directory, String... command) {
        start(console, directory, Arrays.asList(command));
    }

    public void start(Console console, File directory, List<String> command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            pb.directory(directory);
            process = pb.start();
            running = true;
            ExecutableStreamReader.read(process.getInputStream(), console);
            processWriter = new PrintWriter(process.getOutputStream());
        } catch (IOException e) {
            // TODO 例外処理
            e.printStackTrace();
        }
    }

    public void input(String input) {
        processWriter.println(input);
        processWriter.flush();
    }

    public void destroy() {
        process.destroy();
        running = false;
    }

    public void waitFor(WaitCallback callback) {
        if (process != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    process.waitFor();
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

    public boolean isRunning() {
        return running;
    }
}
