package org.sitoolkit.wt.gui.domain.script;

import java.io.File;
import java.util.List;

import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ConversationProcessContainer;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;
import org.sitoolkit.wt.gui.infra.util.StrUtils;

public class ScriptProcessClient {

    public ConversationProcess page2script(String driverType, String baseUrl,
            ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        SitWtRuntimeUtils.addVmArgs(command, driverType, baseUrl);
        command.add("org.sitoolkit.wt.app.page2script.Page2Script");

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

        command.add("org.sitoolkit.wt.app.ope2script.FirefoxOpener");

        ProcessParams params = new ProcessParams();
        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

        return process;
    }

    public void readCaseNo(File testScript, ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();

        command.add("org.sitoolkit.wt.app.test.TestCaseReader");
        command.add(testScript.getAbsolutePath());

        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

    }
}
