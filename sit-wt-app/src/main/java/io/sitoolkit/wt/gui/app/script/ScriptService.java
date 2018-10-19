package io.sitoolkit.wt.gui.app.script;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.sitoolkit.wt.app.config.BaseConfig;
import io.sitoolkit.wt.app.config.ExtConfig;
import io.sitoolkit.wt.app.config.TestScriptConfig;
import io.sitoolkit.wt.app.ope2script.FirefoxOpener;
import io.sitoolkit.wt.app.page2script.Page2Script;
import io.sitoolkit.wt.app.page2script.Page2ScriptConfig;
import io.sitoolkit.wt.app.test.TestCaseReader;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.gui.domain.script.CaseNoCache;
import io.sitoolkit.wt.gui.infra.config.PropertyManager;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import io.sitoolkit.wt.util.infra.process.ProcessExitCallback;

public class ScriptService {

    CaseNoCache cache = new CaseNoCache();

    TestScriptDao dao;

    ApplicationContext appCtx;

    io.sitoolkit.wt.infra.PropertyManager runtimePm;

    Page2Script page2script;

    FirefoxOpener firefoxOpener = new FirefoxOpener();

    TestCaseReader testCaseReader = new TestCaseReader();

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
        dao.write(testScript.getScriptFile(), testScript.getTestStepList(), testScript.getHeaders(), true);
    }

    private synchronized boolean initialized() {
        return dao != null;
    }

    public void page2script(String driverType, String baseUrl, ProcessExitCallback callback) {
        System.setProperty("driver.type", driverType);
        System.setProperty("baseUrl", baseUrl);

        ExecutorContainer.get().execute(() -> {
            ConfigurableApplicationContext pageCtx = new AnnotationConfigApplicationContext(
                    Page2ScriptConfig.class, ExtConfig.class);
            page2script = pageCtx.getBean(Page2Script.class);
            page2script.setOpenScript(true);
            int retcode = page2script.internalExecution();
            pageCtx.close();
            callback.callback(retcode);
        });

    }

    public void ope2script(String baseUrl) {
        System.setProperty("baseUrl", baseUrl);
        ExecutorContainer.get().execute(() -> {
            firefoxOpener.open();
        });
    }

    public List<String> readCaseNo(File testScript) {

        List<String> caseNos = cache.getCaseNosIfNotModified(testScript);

        if (caseNos != null) {
            return caseNos;
        }

        List<String> readCaseNos = testCaseReader.getTestCase(testScript.getAbsolutePath());
        cache.putCaesNos(testScript, readCaseNos);

        return readCaseNos;
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

    public void export() {
        page2script.exportScript();
    }

    public void quitBrowsing() {
        page2script.quitBrowsing();
    }
}
