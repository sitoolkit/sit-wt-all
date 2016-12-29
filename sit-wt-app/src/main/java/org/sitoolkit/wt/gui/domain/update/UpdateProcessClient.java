package org.sitoolkit.wt.gui.domain.update;

import java.io.File;
import java.util.List;

import org.sitoolkit.wt.gui.infra.maven.MavenUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ConversationProcessContainer;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class UpdateProcessClient {

    public UpdateProcessClient() {
    }

    public void checkVersion(File pomFile, VersionCheckMode mode, ProcessParams params) {

        List<String> command = MavenUtils.getCommand(params);
        command.add(mode.getPluginGoal());
        command.add("-f");
        command.add(pomFile.getAbsolutePath());
        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

    }

    public void dependencyCopy(File destDir, String artifact, ProcessParams params) {

        List<String> command = MavenUtils.getCommand(params);
        command.add("dependency:copy");
        command.add("-Dartifact=" + artifact);
        command.add("-DoutputDirectory=" + destDir.getAbsolutePath());
        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);
    }
}
