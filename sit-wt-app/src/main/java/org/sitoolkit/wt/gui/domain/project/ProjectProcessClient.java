package org.sitoolkit.wt.gui.domain.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class ProjectProcessClient {

    public void unpack(File pomFile, ProcessParams params) {

        List<String> command = new ArrayList<>();

        command.add(MavenUtils.getCommand());
        command.add("-Punpack-property-resources");
        command.add("-f");
        command.add(pomFile.getAbsolutePath());
        params.setCommand(command);

        ConversationProcess process = new ConversationProcess();
        process.start(params);
    }
}
