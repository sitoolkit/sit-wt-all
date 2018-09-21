package io.sitoolkit.wt.gui.domain.script;

import java.io.File;
import java.util.List;

import io.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import io.sitoolkit.wt.util.infra.process.ConversationProcess;
import io.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import io.sitoolkit.wt.util.infra.process.ProcessParams;
import io.sitoolkit.wt.util.infra.util.StrUtils;

public class ScriptProcessClient {

    public ConversationProcess page2script(String driverType, String baseUrl,
            ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        SitWtRuntimeUtils.addVmArgs(command, driverType, baseUrl);
        command.add("io.sitoolkit.wt.app.page2script.Page2Script");

        ConversationProcess process = ConversationProcessContainer.create();
        params.setCommand(command);

        process.start(params);

        return process;
    }

    public ConversationProcess ope2script(String baseUrl) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();

        if (StrUtils.isNotEmpty(baseUrl)) {
            command.add("-DbaseUrl=" + baseUrl);
        }

        command.add("io.sitoolkit.wt.app.ope2script.FirefoxOpener");

        ProcessParams params = new ProcessParams();
        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

        return process;
    }

    public void readCaseNo(File testScript, ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();

        command.add("io.sitoolkit.wt.app.test.TestCaseReader");
        command.add(testScript.getAbsolutePath());

        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

    }
}
