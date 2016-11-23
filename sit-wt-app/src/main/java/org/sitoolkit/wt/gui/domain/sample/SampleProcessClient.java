package org.sitoolkit.wt.gui.domain.sample;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class SampleProcessClient {

    public SampleProcessClient() {
    }

    public void create(File destDir, ProcessParams params) {
        params.setDirectory(destDir);

        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        command.add("org.sitoolkit.wt.app.sample.SampleManager");
        params.setCommand(command);

        ConversationProcess process = new ConversationProcess();
        process.start(params);
    }

    public ConversationProcess start(File sampleDir, ProcessParams params) {

        List<String> command = new ArrayList<>();
        command.add(MavenUtils.getCommand());

        params.setCommand(command);
        params.setDirectory(sampleDir);

        ConversationProcess process = new ConversationProcess();
        process.start(params);

        return process;
    }

    public void stop(ProcessParams params) {

        List<String> command = new ArrayList<>();
        command.add(MavenUtils.getCommand());
        command.add("jetty:stop");

        params.setCommand(command);

        ConversationProcess process = new ConversationProcess();
        process.start(params);

    }
}
