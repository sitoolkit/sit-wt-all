package org.sitoolkit.wt.gui.app.test;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.domain.test.SitWtDebugStdoutListener;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeProcessClient;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.domain.test.TestRunParams;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;
import org.sitoolkit.wt.gui.infra.util.FileIOUtils;
import org.sitoolkit.wt.gui.infra.util.LogUtils;
import org.sitoolkit.wt.gui.infra.util.SystemUtils;

public class TestService {

    private static final Logger LOG = LogUtils.get(TestService.class);

    private static final String SCRIPT_TEMPLATE = "TestScriptTemplate.xlsx";

    SitWtRuntimeProcessClient client = new SitWtRuntimeProcessClient();

    public ConversationProcess runTest(TestRunParams params, SitWtDebugStdoutListener listener,
            ProcessExitCallback callback) {

        List<File> testScripts = SitWtRuntimeUtils.filterTestScripts(params.getScripts());

        if (testScripts.isEmpty()) {
            return null;
        }

        ProcessParams processParams = new ProcessParams();

        processParams.getStdoutListeners().add(listener);
        processParams.getExitClallbacks().add(callback);

        return client.runTest(params, processParams);

    }

    public void createNewScript(File baseDir, File destFile) {

        File dir = new File(SystemUtils.getSitRepository(), "sit-wt");
        if (!dir.exists()) {
            LOG.log(Level.INFO, "mkdir sit-wt repo {0}", dir.getAbsolutePath());
            dir.mkdir();
        }

        File template = new File(dir, SCRIPT_TEMPLATE);

        if (template.exists()) {

            FileIOUtils.copy(template, destFile);

        } else {

            ProcessParams params = new ProcessParams();
            params.setDirectory(baseDir);

            params.getExitClallbacks().add(exitCode -> {

                File testscript = new File(baseDir, "target/" + SCRIPT_TEMPLATE);
                LOG.log(Level.INFO, "{0} rename to {1}",
                        new Object[] { testscript.getAbsolutePath(), template.getAbsolutePath() });
                testscript.renameTo(template);

                FileIOUtils.copy(template, destFile);

            });

            client.unpackTestScript(params);

            // ConversationProcess process = new ConversationProcess();
            // process.start(new LogConsole(), baseDir,
            // SitWtRuntimeUtils.buildUnpackTestscriptCommand());
            //
            // process.onExit(exitCode -> {
            // File testscript = new File(baseDir, "target/" + SCRIPT_TEMPLATE);
            // LOG.log(Level.INFO, "{0} rename to {1}",
            // new Object[] { testscript.getAbsolutePath(),
            // template.getAbsolutePath() });
            // testscript.renameTo(template);
            //
            // FileIOUtils.copy(template, destFile);
            // });
        }

    }

}
