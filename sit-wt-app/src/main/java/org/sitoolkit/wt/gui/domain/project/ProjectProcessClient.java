package org.sitoolkit.wt.gui.domain.project;

import java.io.File;
import java.util.List;

import org.sitoolkit.wt.util.infra.maven.MavenUtils;
import org.sitoolkit.wt.util.infra.process.ConversationProcess;
import org.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import org.sitoolkit.wt.util.infra.process.ProcessParams;

public class ProjectProcessClient {

    /**
     * 次のコマンドを実行します。
     *
     * <pre>
     * mvn -f ${pomFile} -P unpack-property-resources
     * </pre>
     *
     * @param pomFile
     *            pom.xml
     * @param params
     *            プロセス実行パラメーター
     */
    public void unpack(File pomFile, ProcessParams params) {

        List<String> command = MavenUtils.getCommand(params);

        command.add("-Punpack-property-resources");
        command.add("-f");
        command.add(pomFile.getAbsolutePath());
        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);
    }
}
