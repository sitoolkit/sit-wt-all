package io.sitoolkit.wt.gui.domain.sample;

import java.io.File;
import java.util.List;

import io.sitoolkit.wt.gui.domain.test.SitWtRuntimeUtils;
import io.sitoolkit.wt.util.infra.maven.MavenUtils;
import io.sitoolkit.wt.util.infra.process.ConversationProcess;
import io.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import io.sitoolkit.wt.util.infra.process.ProcessParams;

public class SampleProcessClient {

    public SampleProcessClient() {
    }

    /**
     * 次のコマンドを実行します。
     *
     * <pre>
     * cd $[destDir}
     * java -cp ${sitWtClasspath} io.sitoolkit.wt.app.sample.SampleManager
     * </pre>
     *
     * @param destDir
     *            サンプル展開先のディレクトリ
     * @param params
     *            プロセス実行パラメーター
     */
    public void create(File destDir, ProcessParams params) {
        params.setDirectory(destDir);

        List<String> command = SitWtRuntimeUtils.buildJavaCommand();
        command.add("io.sitoolkit.wt.app.sample.SampleManager");
        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);
    }

    /**
     * 次のコマンドを実行します。
     *
     * <pre>
     * mvn
     * </pre>
     *
     * @param params
     *            プロセス実行パラメーター
     * @return 対話プロセス
     */
    public ConversationProcess start(ProcessParams params) {

        List<String> command = MavenUtils.getCommand(params);

        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

        return process;
    }

    /**
     * 次のコマンドを実行します。
     *
     * <pre>
     * mvn jetty:stop
     * </pre>
     *
     * @param params
     *            プロセス実行パラメーター
     */
    public void stop(ProcessParams params) {

        List<String> command = MavenUtils.getCommand(params);
        command.add("jetty:stop");

        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

    }
}
