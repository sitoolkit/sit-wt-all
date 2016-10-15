package org.sitoolkit.wt.infra.process;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessUtils {

    static final Logger LOG = LoggerFactory.getLogger(ProcessUtils.class);

    public static void exec(String... command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            Process process = pb.start();
            process.waitFor(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn("", e);
        }
    }

    public static void execute(String... command) {
        execute(true, command);
    }

    public static ExecuteResult execute(boolean wait, String... command) {
        ProcessBuilder builder = new ProcessBuilder(command);

        try {
            String commandStr = toCommandString(command);
            LOG.debug("execute : {}", commandStr);

            Process process = builder.start();

            Executor executor = Executors.newCachedThreadPool();

            StreamReader stdout = new StreamReader(process.toString(), process.getInputStream());
            executor.execute(stdout);
            StreamReader stderr = new StreamReader(process.toString(), process.getInputStream());
            executor.execute(stderr);

            if (!wait) {
                return new ExecuteResult(stdout.getText(), stderr.getText(), 0);
            }

            if (process.waitFor(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("コマンド実行がタイムアウトしました " + commandStr);
            }
            int exitValue = process.exitValue();

            return new ExecuteResult(stdout.getText(), stderr.getText(), exitValue);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toCommandString(String... command) {
        StringBuilder sb = new StringBuilder();

        for (String cmd : command) {

            if (sb.length() > 0) {
                sb.append(" ");
            }

            sb.append("\"");
            sb.append(cmd);
            sb.append("\"");
        }

        return sb.toString();
    }
}
