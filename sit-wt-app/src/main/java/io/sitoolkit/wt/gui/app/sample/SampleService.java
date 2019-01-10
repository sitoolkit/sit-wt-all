package io.sitoolkit.wt.gui.app.sample;

import java.io.File;
import java.nio.file.Path;

import io.sitoolkit.util.buidtoolhelper.process.ProcessExitCallback;
import io.sitoolkit.wt.app.sample.SampleManager;
import io.sitoolkit.wt.gui.domain.sample.SampleProcessClient;
import io.sitoolkit.wt.gui.domain.sample.SampleStartedCallback;

public class SampleService {

    SampleProcessClient client = new SampleProcessClient();

    SampleManager sampleManager = new SampleManager();

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
     */
    public void create(File destDir) {
        Path sampledir = getSampleDir(destDir.toPath());
        if (!sampledir.toFile().exists()) {
            sampledir.toFile().mkdirs();
        }
        sampleManager.unarchiveBasicSample(destDir.getAbsolutePath());
    }

    public void start(File baseDir, SampleStartedCallback callback) {
        client.start(getSampleDir(baseDir.toPath()), callback);
    }

    public void stop(File baseDir) {
        client.stop(getSampleDir(baseDir.toPath()), null);
    }

    public void stop(File baseDir, ProcessExitCallback callback) {
        client.stop(getSampleDir(baseDir.toPath()), callback);
    }

    private Path getSampleDir(Path baseDir) {
        return baseDir.resolve(baseDir);
    }
}
