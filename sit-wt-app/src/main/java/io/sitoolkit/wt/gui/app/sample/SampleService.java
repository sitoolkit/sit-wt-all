package io.sitoolkit.wt.gui.app.sample;

import java.nio.file.Path;
import javax.annotation.Resource;
import io.sitoolkit.util.buildtoolhelper.process.ProcessExitCallback;
import io.sitoolkit.wt.app.sample.SampleManager;
import io.sitoolkit.wt.gui.domain.sample.SampleProcessClient;
import io.sitoolkit.wt.gui.domain.sample.SampleStartedCallback;

public class SampleService {

  SampleProcessClient client = new SampleProcessClient();

  @Resource
  SampleManager sampleManager;

  /**
   * サンプルWebサイトを{@code destDir}以下に展開します。
   *
   * <pre>
   * ${destDir}
   *   sample
   *    input.html
   *    :
   *   testscript
   *     SampleTestScript.csv
   * </pre>
   *
   * @param destDir サンプルを展開するディレクトリ
   */
  public void create(Path destDir) {
    Path sampledir = getSampleDir(destDir);
    if (!sampledir.toFile().exists()) {
      sampledir.toFile().mkdirs();
    }
    sampleManager.unarchiveBasicSample(destDir.toString());
  }

  public void start(int port, Path baseDir, SampleStartedCallback callback) {
    client.start(port, getSampleDir(baseDir), callback);
  }

  public void stop() {
    stop(null);
  }

  public void stop(ProcessExitCallback callback) {
    client.stop(callback);
  }

  private Path getSampleDir(Path baseDir) {
    return baseDir.resolve("sample");
  }
}
