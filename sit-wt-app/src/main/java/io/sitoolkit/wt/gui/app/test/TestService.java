package io.sitoolkit.wt.gui.app.test;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.sitoolkit.wt.app.config.RuntimeConfig;
import io.sitoolkit.wt.app.test.TestRunner;
import io.sitoolkit.wt.domain.debug.DebugListener;
import io.sitoolkit.wt.domain.debug.DebugSupport;
import io.sitoolkit.wt.gui.domain.test.SitWtDebugStdoutListener;
import io.sitoolkit.wt.gui.domain.test.SitWtRuntimeProcessClient;
import io.sitoolkit.wt.gui.domain.test.TestRunParams;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import io.sitoolkit.wt.util.infra.process.ConversationProcess;
import io.sitoolkit.wt.util.infra.process.ProcessExitCallback;
import io.sitoolkit.wt.util.infra.process.ProcessParams;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;
import io.sitoolkit.wt.util.infra.util.SystemUtils;
import lombok.Setter;

public class TestService {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(TestService.class);

    private static final String SCRIPT_TEMPLATE = "TestScriptTemplate_"
            + Locale.getDefault().getLanguage() + ".csv";

    SitWtRuntimeProcessClient client = new SitWtRuntimeProcessClient();

    private TestRunner runner = new TestRunner();

    private Map<String, ConfigurableApplicationContext> ctxMap = new HashMap<>();

    @Setter
    private DebugListener debugListener;

    public String runTest(TestRunParams params, ProcessExitCallback callback) {


        if (params.getTargetScripts() == null) {
            return null;
        }

        String sessionId = UUID.randomUUID().toString();

        ExecutorContainer.get().execute(() -> {
            try {
                System.setProperty("driver.type", params.getDriverType());
                System.setProperty("sitwt.projectDirectory", params.getProjectDir().getAbsolutePath());
                String profile = ("android".equals(params.getDriverType()) || "ios".equals(params.getDriverType()))
                        ? "mobile" : "pc";
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
                    debugSupport.setListener(debugListener);
                    runner.runScript(appCtx, params.getTargetScripts(), params.isParallel(), true);
                    callback.callback(0);
                } catch (Exception e) {
                    LOG.error("app.unexpectedException", e);
                    callback.callback(1);
                } finally {
                    appCtx.close();
                    ctxMap.remove(sessionId);
                }
            } catch (Exception e) {
                LOG.error("app.unexpectedException", e);
                callback.callback(1);
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

    @Deprecated
    public ConversationProcess runTest(TestRunParams params, SitWtDebugStdoutListener listener,
            ProcessExitCallback callback) {

        if (params.getTargetScripts() == null) {
            return null;
        }

        ProcessParams processParams = new ProcessParams();

        processParams.getStdoutListeners().add(listener);
        processParams.getExitClallbacks().add(callback);

        return client.runTest(params, processParams);

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

            ProcessParams params = new ProcessParams();
            params.setDirectory(baseDir);

            params.getExitClallbacks().add(exitCode -> {

                File testscript = new File(baseDir, "target/" + SCRIPT_TEMPLATE);
                LOG.info("app.scriptRename",
                        new Object[] { testscript.getAbsolutePath(), template.getAbsolutePath() });
                testscript.renameTo(template);

                FileIOUtils.copy(template, destFile);

            });

            client.unpackTestScript(params);

        }

    }

}