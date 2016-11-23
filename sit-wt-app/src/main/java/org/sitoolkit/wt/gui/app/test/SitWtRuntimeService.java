package org.sitoolkit.wt.gui.app.test;

import java.io.File;

import org.sitoolkit.wt.gui.domain.test.MavenClasspahListener;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeProcessClient;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class SitWtRuntimeService {

    SitWtRuntimeProcessClient client = new SitWtRuntimeProcessClient();

    public SitWtRuntimeService() {
    }

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
