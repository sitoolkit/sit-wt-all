package org.sitoolkit.wt.gui.domain.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class SitWtRuntimeProcessClient {

    public SitWtRuntimeProcessClient() {
        // TODO Auto-generated constructor stub
    }

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

}
