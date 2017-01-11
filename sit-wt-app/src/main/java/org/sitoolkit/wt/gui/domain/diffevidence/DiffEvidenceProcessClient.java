package org.sitoolkit.wt.gui.domain.diffevidence;

import java.io.File;
import java.util.List;

import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ConversationProcessContainer;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class DiffEvidenceProcessClient {

    public void genMaskEvidence(File targetDir, ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        command.add("org.sitoolkit.wt.app.compareevidence.MaskEvidenceGenerator");
        command.add(targetDir.getPath());

        ConversationProcess process = ConversationProcessContainer.create();
        params.setCommand(command);

        process.start(params);

    }

    public void setBaseEvidence(File targetDir, ProcessParams params) {
        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        command.add("org.sitoolkit.wt.app.compareevidence.BaseEvidenceManager");
        command.add(targetDir.getPath());

        ConversationProcess process = ConversationProcessContainer.create();
        params.setCommand(command);

        process.start(params);

    }

    public void genDiffEvidence(File baseDir, File targetDir, ProcessParams params) {

        List<String> command = SitWtRuntimeUtils.buildJavaCommand();

        command.add("org.sitoolkit.wt.app.compareevidence.DiffEvidenceGenerator");

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
