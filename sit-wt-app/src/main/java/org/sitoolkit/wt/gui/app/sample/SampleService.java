package org.sitoolkit.wt.gui.app.sample;

import java.io.File;

import org.sitoolkit.wt.gui.domain.sample.JettyMavenPluginStdoutListener;
import org.sitoolkit.wt.gui.domain.sample.SampleCreatedCallback;
import org.sitoolkit.wt.gui.domain.sample.SampleProcessClient;
import org.sitoolkit.wt.gui.domain.sample.SampleStartedCallback;
import org.sitoolkit.wt.gui.domain.sample.SampleStoppedCallback;
import org.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import org.sitoolkit.wt.util.infra.process.ConversationProcess;
import org.sitoolkit.wt.util.infra.process.ProcessParams;

public class SampleService {

    SampleProcessClient client = new SampleProcessClient();

    /**
     * サンプルWebサイトを{@code destDir}以下に展開します。
     *
     * <pre>
     * ${destDir}
     *   sample
     *    input.html
     *    :
     *    pom.xml
     *   testscript
     *     SampleTestScript.csv
     * </pre>
     *
     * @param destDir
     *            サンプルを展開するディレクトリ
     * @param callback
     *            サンプル展開後に実行されるCallback
     */
    public void create(File destDir, SampleCreatedCallback callback) {
        ProcessParams params = new ProcessParams();

        params.getExitClallbacks().add(exitCode -> {
            File sampledir = getSampleDir(destDir);
            if (!sampledir.exists()) {
                sampledir.mkdirs();
            }
            callback.onCreated(sampledir);
        });

        client.create(destDir, params);
    }

    public ConversationProcess start(File baseDir, SampleStartedCallback callback) {

        ProcessParams params = new ProcessParams();

        params.setDirectory(getSampleDir(baseDir));

        JettyMavenPluginStdoutListener listener = new JettyMavenPluginStdoutListener();
        params.getStdoutListeners().add(listener);

        ExecutorContainer.get().execute(() -> callback.onStarted(listener.isSuccess()));

        return client.start(params);
    }

    public void stop(File baseDir) {
        stop(baseDir, null);
    }

    public void stop(File baseDir, SampleStoppedCallback callback) {

        File sampleDir = getSampleDir(baseDir);
        if (!sampleDir.exists()) {
            return;
        }

        ProcessParams params = new ProcessParams();

        params.setDirectory(sampleDir);

        if (callback != null) {
            params.getExitClallbacks().add(exitCode -> {
                callback.onStopped();
            });
        }

        client.stop(params);
    }

    private File getSampleDir(File baseDir) {
        return new File(baseDir, "sample");
    }
}
