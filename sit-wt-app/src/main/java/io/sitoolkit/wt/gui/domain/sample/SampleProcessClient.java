package io.sitoolkit.wt.gui.domain.sample;

import java.io.File;

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
    public void start(File sampleDir, SampleStartedCallback callback) {

        JettyMavenPluginStdoutListener listener = new JettyMavenPluginStdoutListener();
        MavenProject
            .load(sampleDir.toPath())
            .mvnw("")
            .stdout(listener)
            .executeAsync();

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
    public void stop(File sampleDir, ProcessExitCallback callback) {

        ProcessCommand cmd = MavenProject
            .load(sampleDir.toPath())
            .mvnw("jetty:stop");
        if (callback != null)
            cmd.getExitCallbacks().add(callback);
        cmd.executeAsync();
    }
}
