package io.sitoolkit.wt.gui.app.test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.sitoolkit.wt.app.config.RuntimeConfig;
import io.sitoolkit.wt.app.test.TestRunner;
import io.sitoolkit.wt.domain.debug.DebugSupport;
import io.sitoolkit.wt.gui.domain.test.TestRunParams;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;
import io.sitoolkit.wt.util.infra.util.SystemUtils;

public class TestService {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(TestService.class);

    private static final String SCRIPT_TEMPLATE = "TestScriptTemplate_"
            + Locale.getDefault().getLanguage() + ".csv";

    private TestRunner runner = new TestRunner();

    private Map<String, ConfigurableApplicationContext> ctxMap = new HashMap<>();

    public String runTest(TestRunParams params, TestExitCallback callback) {

        if (params.getTargetScripts() == null) {
            return null;
        }

        String sessionId = UUID.randomUUID().toString();

        ExecutorContainer.get().execute(() -> {
            try {
                System.setProperty("driver.type", params.getDriverType());
                String profile = ("android".equals(params.getDriverType())
                        || "ios".equals(params.getDriverType())) ? "mobile" : "pc";
                AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext();
                appCtx.register(RuntimeConfig.class);
                appCtx.getEnvironment().addActiveProfile(profile);
                appCtx.refresh();
                ctxMap.put(sessionId, appCtx);
                try {
                    PropertyManager runtimePm = appCtx.getBean(PropertyManager.class);
                    runtimePm.setBaseUrl(params.getBaseUrl());
                    runtimePm.setDebug(params.isDebug());
                    DebugSupport debugSupport = appCtx.getBean(DebugSupport.class);
                    debugSupport.setListener(params.getDebugListener());
                    callback.callback(runner.runScript(appCtx, params.getTargetScripts(),
                            params.isParallel(), false));
                } catch (Exception e) {
                    LOG.error("app.unexpectedException", e);
                    callback.callback(Collections.emptyList());
                } finally {
                    appCtx.close();
                    ctxMap.remove(sessionId);
                }
            } catch (Exception e) {
                LOG.error("app.unexpectedException", e);
                callback.callback(Collections.emptyList());
            }
        });

        return sessionId;

    }

    public void pause(String sessionId) {
        getDebugSupport(sessionId).pause();
    }

    public void restart(String sessionId, String stepNo) {
        getDebugSupport(sessionId).restart(stepNo);
    }

    public void forward(String sessionId) {
        getDebugSupport(sessionId).forward();
    }

    public void back(String sessionId) {
        getDebugSupport(sessionId).back();
    }

    public void export(String sessionId) {
        getDebugSupport(sessionId).export();
    }

    public void checkLocator(String sessionId, String locatorStr) {
        getDebugSupport(sessionId).checkLocator(locatorStr);
    }

    public void stopTest(String sessionId) {
        ConfigurableApplicationContext appCtx = ctxMap.get(sessionId);
        appCtx.close();
        ctxMap.remove(sessionId);
    }

    public void destroy() {
        ctxMap.values().stream().forEach(ConfigurableApplicationContext::close);
    }

    private DebugSupport getDebugSupport(String sessionId) {
        ConfigurableApplicationContext appCtx = ctxMap.get(sessionId);
        return appCtx.getBean(DebugSupport.class);
    }

    public void createNewScript(File baseDir, File destFile) {

        File dir = new File(SystemUtils.getSitRepository(), "sit-wt");
        if (!dir.exists()) {
            LOG.info("app.makeSitwtDir", dir.getAbsolutePath());
            dir.mkdir();
        }

        File template = new File(dir, SCRIPT_TEMPLATE);

        if (template.exists()) {

            FileIOUtils.copy(template, destFile);

        } else {
            FileIOUtils.sysRes2file(SCRIPT_TEMPLATE, destFile.toPath());

        }

    }

}
