package io.sitoolkit.wt.gui.domain.diffevidence;

import java.io.File;
import java.util.List;

import io.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import io.sitoolkit.wt.util.infra.process.ConversationProcess;
import io.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import io.sitoolkit.wt.util.infra.process.ProcessParams;

public class DiffEvidenceProcessClient {

    public void genMaskEvidence(File targetDir, ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        command.add("io.sitoolkit.wt.app.compareevidence.MaskEvidenceGenerator");
        command.add(targetDir.getPath());

        ConversationProcess process = ConversationProcessContainer.create();
        params.setCommand(command);

        process.start(params);

    }

    public void setBaseEvidence(File targetDir, ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        command.add("io.sitoolkit.wt.app.compareevidence.BaseEvidenceManager");
        command.add(targetDir.getPath());

        ConversationProcess process = ConversationProcessContainer.create();
        params.setCommand(command);

        process.start(params);

    }

    public void genDiffEvidence(File baseDir, File targetDir, ProcessParams params) {

        List<String> command = SitWtRuntimeUtils.buildJavaCommand();

        command.add("io.sitoolkit.wt.app.compareevidence.DiffEvidenceGenerator");

        if (baseDir != null) {
            command.add(baseDir.getPath());
        }
        if (targetDir != null) {
            command.add(targetDir.getPath());
        }

        ConversationProcess process = ConversationProcessContainer.create();
        params.setCommand(command);

        process.start(params);

    }
}
