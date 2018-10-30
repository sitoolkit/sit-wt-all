package io.sitoolkit.wt.gui.app.sample;

import java.io.File;

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
     * @param callback
     *            サンプル展開後に実行されるCallback
     */
    public void create(File destDir) {
        File sampledir = getSampleDir(destDir);
        if (!sampledir.exists()) {
            sampledir.mkdirs();
        }
        sampleManager.unarchiveBasicSample(destDir.getAbsolutePath());
    }

    public void start(File baseDir, SampleStartedCallback callback) {
        client.start(getSampleDir(baseDir), callback);
    }

    public void stop(File baseDir) {
        client.stop(getSampleDir(baseDir), null);
    }

    public void stop(File baseDir, ProcessExitCallback callback) {
        client.stop(getSampleDir(baseDir), callback);
    }

    private File getSampleDir(File baseDir) {
        return new File(baseDir, "sample");
    }
}
