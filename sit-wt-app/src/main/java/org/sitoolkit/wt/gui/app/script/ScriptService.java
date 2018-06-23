package org.sitoolkit.wt.gui.app.script;

import java.io.File;
import java.util.List;

import org.sitoolkit.wt.app.config.BaseConfig;
import org.sitoolkit.wt.app.config.TestScriptConfig;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.gui.domain.script.CaseNoCache;
import org.sitoolkit.wt.gui.domain.script.CaseNoReadCallback;
import org.sitoolkit.wt.gui.domain.script.CaseNoStdoutListener;
import org.sitoolkit.wt.gui.domain.script.ScriptProcessClient;
import org.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import org.sitoolkit.wt.util.infra.process.ConversationProcess;
import org.sitoolkit.wt.util.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.util.infra.process.ProcessParams;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ScriptService {

    CaseNoCache cache = new CaseNoCache();

    ScriptProcessClient client = new ScriptProcessClient();

    TestScriptDao dao;

    public ScriptService() {
        initialize();
    }

    private void initialize() {
        ExecutorContainer.get().execute(() -> {
            ApplicationContext appCtx = new AnnotationConfigApplicationContext(BaseConfig.class,
                    TestScriptConfig.class);
            dao = appCtx.getBean(TestScriptDao.class);
        });
    }

    public TestScript read(File file) {
        while (!initialized()) {

        }
        return dao.load(file, "TestScript", false);
    }

    public void write(TestScript testScript) {
        dao.write(testScript.getScriptFile(), testScript.getTestStepList(), true);
    }

    private synchronized boolean initialized() {
        return dao != null;
    }

    public ConversationProcess page2script(String driverType, String baseUrl,
            ProcessExitCallback callback) {

        ProcessParams params = new ProcessParams();
        params.getExitClallbacks().add(callback);

        return client.page2script(driverType, baseUrl, params);
    }

    public ConversationProcess ope2script(String baseUrl) {
        return client.ope2script(baseUrl);
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
