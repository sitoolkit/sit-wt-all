package io.sitoolkit.wt.gui.app.script;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.sitoolkit.wt.app.config.BaseConfig;
import io.sitoolkit.wt.app.config.ExtConfig;
import io.sitoolkit.wt.app.ope2script.FirefoxOpener;
import io.sitoolkit.wt.app.page2script.Page2Script;
import io.sitoolkit.wt.app.page2script.Page2ScriptConfig;
import io.sitoolkit.wt.app.test.TestCaseReader;
import io.sitoolkit.wt.app.test.TestScriptConfig;
import io.sitoolkit.wt.app.test.TestScriptGenerator;
import io.sitoolkit.wt.domain.operation.OperationConverter;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.gui.domain.script.CaseNoCache;
import io.sitoolkit.wt.gui.infra.config.PropertyManager;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;

public class ScriptService {

    CaseNoCache cache = new CaseNoCache();

    TestScriptDao dao;

    ApplicationContext appCtx;

    io.sitoolkit.wt.infra.PropertyManager runtimePm;

    Page2Script page2script;

    FirefoxOpener firefoxOpener = new FirefoxOpener();

    ConfigurableApplicationContext pageCtx;

    TestCaseReader testCaseReader = new TestCaseReader();

    OperationConverter operationConverter = new OperationConverter();

    TestScriptGenerator testScriptGenerator;
    
    public ScriptService() {
        initialize();
    }

    private void initialize() {
        ExecutorContainer.get().execute(() -> {
            appCtx = new AnnotationConfigApplicationContext(BaseConfig.class,
                    TestScriptConfig.class);
            dao = appCtx.getBean(TestScriptDao.class);
            runtimePm  = appCtx.getBean(io.sitoolkit.wt.infra.PropertyManager.class);
            testScriptGenerator = appCtx.getBean(TestScriptGenerator.class);
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

    public void page2script(String driverType, String baseUrl) {
        pageCtx = new AnnotationConfigApplicationContext(Page2ScriptConfig.class, ExtConfig.class);
        page2script = pageCtx.getBean(Page2Script.class);
        page2script.setOpenScript(false);
        page2script.openBrowser(baseUrl, driverType);

    }

    public void ope2script(String baseUrl) {
        ExecutorContainer.get().execute(() -> {
            firefoxOpener.open(baseUrl);
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

    public Path export() {
        page2script.generateFromPage();
        return page2script.getCreateFile();
    }

    public void quitBrowsing() {
        pageCtx.close();
    }
    
    public void generateNewScript(Path destFile) {
        testScriptGenerator.generateNewScript(destFile);
    }

}
