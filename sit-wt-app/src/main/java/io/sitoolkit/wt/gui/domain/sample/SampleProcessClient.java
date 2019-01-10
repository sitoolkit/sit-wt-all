package io.sitoolkit.wt.gui.domain.sample;

import java.nio.file.Path;

import io.sitoolkit.util.buidtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buidtoolhelper.process.ProcessCommand;
import io.sitoolkit.util.buidtoolhelper.process.ProcessExitCallback;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;

public class SampleProcessClient {

    public SampleProcessClient() {
    }

    /**
     * 次のコマンドを実行します。
     *
     * <pre>
     * mvn
     * </pre>
     *
     */
    public void start(Path sampleDir, SampleStartedCallback callback) {

        JettyMavenPluginStdoutListener listener = new JettyMavenPluginStdoutListener();
        MavenProject.load(sampleDir).mvnw().stdout(listener).executeAsync();

        ExecutorContainer.get().execute(() -> {
            callback.onStarted(listener.isSuccess());
        });
    }

    /**
     * 次のコマンドを実行します。
     *
     * <pre>
     * mvn jetty:stop
     * </pre>
     *
     */
    public void stop(Path sampleDir, ProcessExitCallback callback) {
        if (!sampleDir.toFile().exists()) {
            callback.callback(0);
        }

        ProcessCommand cmd = MavenProject.load(sampleDir).mvnw("jetty:stop");
        if (callback != null)
            cmd.getExitCallbacks().add(callback);
        cmd.executeAsync();
    }
}
