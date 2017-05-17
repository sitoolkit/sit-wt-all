package org.sitoolkit.wt.gui.app.test;

import java.io.File;

import org.sitoolkit.wt.gui.domain.test.MavenClasspahListener;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeProcessClient;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.util.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.util.infra.process.ProcessParams;

public class SitWtRuntimeService {

    SitWtRuntimeProcessClient client = new SitWtRuntimeProcessClient();

    public SitWtRuntimeService() {
    }

    /**
     * {@code pomFile}のclasspathを{@link SitWtRuntimeUtils}に設定します。
     *
     * <h3>処理順</h3>
     * <ol>
     * <li>mvn dependency:build-classpath -f ${pomFile}
     * </ol>
     *
     * @param pomFile
     *            プロジェクトのpom.xml
     * @param exitCallback
     *            クラスパス取得後のCallback
     */
    public void loadClasspath(File pomFile, ProcessExitCallback exitCallback) {

        ProcessParams params = new ProcessParams();
        MavenClasspahListener classpathListener = new MavenClasspahListener();
        params.getStdoutListeners().add(classpathListener);
        params.getExitClallbacks().add(exitCallback);

        params.getExitClallbacks().add(exitCode -> {
            SitWtRuntimeUtils.setSitWtClasspath(classpathListener.getClasspath());
        });

        client.buildClasspath(pomFile, params);

    }

}
