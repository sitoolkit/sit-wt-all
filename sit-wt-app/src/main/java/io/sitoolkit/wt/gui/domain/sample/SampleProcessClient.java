package io.sitoolkit.wt.gui.domain.sample;

import java.util.ArrayList;
import java.util.List;

import io.sitoolkit.util.buidtoolhelper.maven.MavenUtils;
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
     * mvn
     * </pre>
     *
     * @param params
     *            プロセス実行パラメーター
     * @return 対話プロセス
     */
    public ConversationProcess start(ProcessParams params) {

        List<String> command = new ArrayList<String>();
        command.add(MavenUtils.getCommand());

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

        List<String> command = new ArrayList<String>();
        command.add(MavenUtils.getCommand());
        command.add("jetty:stop");

        params.setCommand(command);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

    }
}
