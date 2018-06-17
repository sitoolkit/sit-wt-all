package org.sitoolkit.wt.gui.app.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.app.config.RuntimeConfig;
import org.sitoolkit.wt.app.test.TestRunner;
import org.sitoolkit.wt.domain.debug.DebugSupport;
import org.sitoolkit.wt.gui.domain.test.SitWtDebugStdoutListener;
import org.sitoolkit.wt.gui.domain.test.SitWtRuntimeProcessClient;
import org.sitoolkit.wt.gui.domain.test.TestRunParams;
import org.sitoolkit.wt.gui.infra.log.LogUtils;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;
import org.sitoolkit.wt.util.infra.process.ConversationProcess;
import org.sitoolkit.wt.util.infra.process.ProcessExitCallback;
import org.sitoolkit.wt.util.infra.process.ProcessParams;
import org.sitoolkit.wt.util.infra.util.FileIOUtils;
import org.sitoolkit.wt.util.infra.util.SystemUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestService {

    private static final Logger LOG = LogUtils.get(TestService.class);

    private static final String SCRIPT_TEMPLATE = "TestScriptTemplate.xlsx";

    SitWtRuntimeProcessClient client = new SitWtRuntimeProcessClient();

    private TestRunner runner = new TestRunner();

    private Map<String, ConfigurableApplicationContext> ctxMap = new HashMap<>();

    public String runTest(TestRunParams params, ProcessExitCallback callback) {

        if (params.getTargetScripts() == null) {
            return null;
        }

        ConfigurableApplicationContext appCtx = new AnnotationConfigApplicationContext(
                RuntimeConfig.class);

        String sessionId = UUID.randomUUID().toString();

        ctxMap.put(sessionId, appCtx);

        ExecutorContainer.get().execute(() -> {
            try {

                PropertyManager runtimePm = appCtx.getBean(PropertyManager.class);
                runtimePm.setBaseUrl(params.getBaseUrl());
                runtimePm.setDriverType(params.getDriverType());
                runtimePm.setDebug(params.isDebug());

                runner.runScript(appCtx, params.getTargetScripts(), params.isParallel(), true);
                callback.callback(0);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "unexpected exception", e);
                callback.callback(1);
            } finally {
                appCtx.close();
                ctxMap.remove(sessionId);
            }
        });

        return sessionId;

    }

    public void pause(String sessionId) {
        ConfigurableApplicationContext appCtx = ctxMap.get(sessionId);

        DebugSupport debug = appCtx.getBean(DebugSupport.class);
        debug.pause();
    }

    public void restart(String sessionId) {
        ConfigurableApplicationContext appCtx = ctxMap.get(sessionId);

        DebugSupport debug = appCtx.getBean(DebugSupport.class);
        debug.setPaused(false);
    }
    
    public void forward(String sessionId) {
        ConfigurableApplicationContext appCtx = ctxMap.get(sessionId);
  
        DebugSupport debug = appCtx.getBean(DebugSupport.class);
        debug.forward();
    }
    
    public void back(String sessionId) {
        ConfigurableApplicationContext appCtx = ctxMap.get(sessionId);
  
        DebugSupport debug = appCtx.getBean(DebugSupport.class);
        debug.back();
    }

    public void stopTest(String sessionId) {
        ConfigurableApplicationContext appCtx = ctxMap.get(sessionId);
        appCtx.close();
        ctxMap.remove(sessionId);
    }

    public void destroy() {
        ctxMap.values().stream().forEach(ConfigurableApplicationContext::close);
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
            LOG.log(Level.INFO, "mkdir sit-wt repo {0}", dir.getAbsolutePath());
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
                LOG.log(Level.INFO, "{0} rename to {1}",
                        new Object[] { testscript.getAbsolutePath(), template.getAbsolutePath() });
                testscript.renameTo(template);

                FileIOUtils.copy(template, destFile);

            });

            client.unpackTestScript(params);

        }

    }

}
