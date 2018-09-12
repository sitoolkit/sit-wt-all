package io.sitoolkit.wt.gui.app.script;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.sitoolkit.wt.app.config.BaseConfig;
import io.sitoolkit.wt.app.config.TestScriptConfig;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.gui.domain.script.CaseNoCache;
import io.sitoolkit.wt.gui.domain.script.CaseNoReadCallback;
import io.sitoolkit.wt.gui.domain.script.CaseNoStdoutListener;
import io.sitoolkit.wt.gui.domain.script.ScriptProcessClient;
import io.sitoolkit.wt.gui.infra.config.PropertyManager;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import io.sitoolkit.wt.util.infra.process.ConversationProcess;
import io.sitoolkit.wt.util.infra.process.ProcessExitCallback;
import io.sitoolkit.wt.util.infra.process.ProcessParams;

public class ScriptService {

    CaseNoCache cache = new CaseNoCache();

    ScriptProcessClient client = new ScriptProcessClient();

    TestScriptDao dao;

    ApplicationContext appCtx;

    io.sitoolkit.wt.infra.PropertyManager runtimePm;

    public ScriptService() {
        initialize();
    }

    private void initialize() {
        ExecutorContainer.get().execute(() -> {
            appCtx = new AnnotationConfigApplicationContext(BaseConfig.class,
                    TestScriptConfig.class);
            dao = appCtx.getBean(TestScriptDao.class);
            runtimePm  = appCtx.getBean(io.sitoolkit.wt.infra.PropertyManager.class);
        });
    }

    public void loadProject() {
        PropertyManager pm = PropertyManager.get();
        runtimePm.setCsvCharset(pm.getCsvCharset());
        runtimePm.setCsvHasBOM(pm.getCsvHasBOM());
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

    public void write(TestScript testScript, Optional<ScriptFileType> scriptFileType) {
        doWithScriptFileType(scriptFileType, () -> write(testScript));
    }

    public TestScript read(File file, Optional<ScriptFileType> scriptFileType) {
        return doWithScriptFileType(scriptFileType, () -> read(file));
    }

    private <T> T doWithScriptFileType (Optional<ScriptFileType> scriptFileType, Supplier<T> s) {
        Charset charset = runtimePm.getCsvCharset();
        boolean hasBom = runtimePm.isCsvHasBOM();
        scriptFileType.ifPresent(ft -> {
            if (ft.isTextFile()) {
                runtimePm.setCsvCharset(ft.getCharset());
                runtimePm.setCsvHasBOM(ft.isHasBom());
            }
        });
        T result = s.get();
        runtimePm.setCsvCharset(charset);
        runtimePm.setCsvHasBOM(hasBom);
        return result;
    }

    private void doWithScriptFileType (Optional<ScriptFileType> scriptFileType, Runnable r) {
        doWithScriptFileType(scriptFileType, () -> {
            r.run();
            return null;
        });
    }

}
