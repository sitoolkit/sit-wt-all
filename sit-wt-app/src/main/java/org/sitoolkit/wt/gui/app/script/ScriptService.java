package org.sitoolkit.wt.gui.app.script;

import java.io.File;
import java.util.List;

import org.sitoolkit.wt.gui.domain.script.CaseNoCache;
import org.sitoolkit.wt.gui.domain.script.CaseNoReadCallback;
import org.sitoolkit.wt.gui.domain.script.CaseNoStdoutListener;
import org.sitoolkit.wt.gui.domain.script.ScriptProcessClient;
import org.sitoolkit.wt.gui.infra.process.ConversationProcess;
import org.sitoolkit.wt.gui.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.gui.infra.process.ProcessParams;

public class ScriptService {

    CaseNoCache cache = new CaseNoCache();

    ScriptProcessClient client = new ScriptProcessClient();

    public ConversationProcess page2script(String driverType, String baseUrl,
            ProcessExitCallback callback) {

        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        return client.page2script(driverType, baseUrl, params);
    }

    public ConversationProcess ope2script(String url) {
        return client.ope2script(url);
    }

    public void readCaseNo(File testScript, CaseNoReadCallback callback) {

        List<String> caseNos = cache.getCaseNosIfNotModified(testScript);

        if (caseNos != null) {
            callback.onRead(caseNos);
            return;
        }

        ProcessParams params = new ProcessParams();

        CaseNoStdoutListener caseNoStdoutListener = new CaseNoStdoutListener();
        params.getStdoutListeners().add(caseNoStdoutListener);

        params.getExitClallbacks().add(exitCode -> {

            if (exitCode == 0) {
                List<String> readCaseNos = caseNoStdoutListener.getCaseNoList();
                cache.putCaesNos(testScript, readCaseNos);
                callback.onRead(readCaseNos);
            } else {
                // TODO 例外処理
            }

        });

        client.readCaseNo(testScript, params);
    }
}
