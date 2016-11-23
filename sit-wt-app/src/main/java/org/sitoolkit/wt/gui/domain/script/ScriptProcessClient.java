package org.sitoolkit.wt.gui.domain.script;

import java.util.List;

import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;
import org.sitoolkit.wt.gui.infra.util.StrUtils;

public class ScriptProcessClient {

    public ConversationProcess page2script(String driverType, String baseUrl,
            ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        SitWtRuntimeUtils.addVmArgs(command, driverType, baseUrl);
        command.add("org.sitoolkit.wt.app.page2script.Page2Script");

        ConversationProcess process = new ConversationProcess();
        params.setCommand(command);

        process.start(params);

        return process;
    }

    public ConversationProcess ope2script(String url) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();

        if (StrUtils.isNotEmpty(url)) {
            command.add("-Durl=" + url);
        }

        command.add("org.sitoolkit.wt.app.ope2script.FirefoxOpener");

        ProcessParams params = new ProcessParams();
        params.setCommand(command);

        ConversationProcess process = new ConversationProcess();
        process.start(params);

        return process;
    }
}
