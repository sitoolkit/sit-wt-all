package io.sitoolkit.wt.gui.domain.test;

import java.io.File;
import java.util.List;

import io.sitoolkit.wt.util.infra.maven.MavenUtils;
import io.sitoolkit.wt.util.infra.process.ConversationProcess;
import io.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import io.sitoolkit.wt.util.infra.process.ProcessParams;

public class SitWtRuntimeProcessClient {

    /**
     * 次のコマンドを実行します。
     *
     * <pre>
     *  mvn dependency:build-classpath -f ${pomFile}
     * </pre>
     *
     * @param pomFile
     *            pom.xml
     * @param params
     *            プロセス実行パラメーター
     */
    public void buildClasspath(File pomFile, ProcessParams params) {
        List<String> command = MavenUtils.getCommand(params);

        command.add("dependency:build-classpath");
        command.add("-f");
        command.add(pomFile.getAbsolutePath());

        params.setCommand(command);
        params.setDirectory(pomFile.getAbsoluteFile().getParentFile());

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);
    }

    public void unpackTestScript(ProcessParams params) {

        List<String> command = MavenUtils.getCommand(params);
        command.add("-Punpack-testscript");
        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);
    }
}
