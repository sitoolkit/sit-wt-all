package org.sitoolkit.wt.gui.domain.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;
import org.sitoolkit.wt.gui.infra.util.StrUtils;

public class SitWtRuntimeProcessClient {

    public void buildClasspath(File pomFile, ProcessParams params) {
        List<String> command = new ArrayList<>();
        command.add(MavenUtils.getCommand());

        command.add("dependency:build-classpath");
        command.add("-f");
        command.add(pomFile.getAbsolutePath());

        params.setCommand(command);
        params.setDirectory(pomFile.getAbsoluteFile().getParentFile());

        ConversationProcess process = new ConversationProcess();
        process.start(params);
    }

    public ConversationProcess runTest(TestRunParams testRunParams, ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        SitWtRuntimeUtils.addVmArgs(command, testRunParams.getDriverType(),
                testRunParams.getBaseUrl());

        command.add("-cp");
        command.add(
                "src/main/resources" + File.pathSeparator + SitWtRuntimeUtils.getSitWtClasspath());

        if (testRunParams.isDebug()) {
            command.add("-Dsitwt.debug=true");
        }

        if (testRunParams.isParallel()) {
            command.add("-Dsitwt.parallel=true");
        }

        command.add("-Dsitwt.open-evidence=true");

        command.add("org.sitoolkit.wt.app.test.TestRunner");

        command.add(StrUtils.join(testRunParams.getScripts()));

        params.setCommand(command);

        ConversationProcess process = new ConversationProcess();
        process.start(params);

        return process;
    }

    public void unpackTestScript(ProcessParams params) {

        List<String> command = new ArrayList<>();
        command.add(MavenUtils.getCommand());
        command.add("-Punpack-testscript");
        params.setCommand(command);

        ConversationProcess process = new ConversationProcess();
        process.start(params);
    }
}
