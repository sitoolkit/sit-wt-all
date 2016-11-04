package org.sitoolkit.wt.gui.app.test;

import java.util.List;

import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.domain.test.TestRunParams;
import org.sitoolkit.wt.gui.infra.process.Console;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess.OnExitCallback;
import org.sitoolkit.wt.gui.infra.util.StrUtils;

public class TestService {

    public TestService() {
        // TODO Auto-generated constructor stub
    }

    public ConversationProcess runTest(TestRunParams params, Console console, OnExitCallback callback) {

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
}
