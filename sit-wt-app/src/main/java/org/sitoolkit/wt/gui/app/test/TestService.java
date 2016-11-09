package org.sitoolkit.wt.gui.app.test;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.domain.test.TestRunParams;
import org.sitoolkit.wt.gui.infra.process.Console;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess.OnExitCallback;
import org.sitoolkit.wt.gui.infra.process.LogConsole;
import org.sitoolkit.wt.gui.infra.util.FileIOUtils;
import org.sitoolkit.wt.gui.infra.util.LogUtils;
import org.sitoolkit.wt.gui.infra.util.StrUtils;
import org.sitoolkit.wt.gui.infra.util.SystemUtils;

public class TestService {

    private static final Logger LOG = LogUtils.get(TestService.class);

    private static final String SCRIPT_TEMPLATE = "TestScriptTemplate.xlsx";

    public TestService() {
        // TODO Auto-generated constructor stub
    }

    public ConversationProcess runTest(TestRunParams params, Console console,
            OnExitCallback callback) {

        String testedClasses = SitWtRuntimeUtils.findTestedClasses(params.getScripts());

        if (StrUtils.isEmpty(testedClasses)) {
            return null;
        }

        List<String> command = SitWtRuntimeUtils.buildSingleTestCommand(params.getScripts(),
                params.isDebug(), params.isParallel(), params.getDriverType(), params.getBaseUrl());

        ConversationProcess process = new ConversationProcess();
        process.start(console, params.getBaseDir(), command);

        process.onExit(callback);

        return process;
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

            ConversationProcess process = new ConversationProcess();
            process.start(new LogConsole(), baseDir,
                    SitWtRuntimeUtils.buildUnpackTestscriptCommand());

            process.onExit(exitCode -> {
                File testscript = new File(baseDir, "target/" + SCRIPT_TEMPLATE);
                LOG.log(Level.INFO, "{0} rename to {1}",
                        new Object[] { testscript.getAbsolutePath(), template.getAbsolutePath() });
                testscript.renameTo(template);

                FileIOUtils.copy(template, destFile);
            });
        }

    }

}
