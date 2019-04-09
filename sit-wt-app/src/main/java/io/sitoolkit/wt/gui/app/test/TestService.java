package io.sitoolkit.wt.gui.app.test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.sitoolkit.wt.app.config.RuntimeConfig;
import io.sitoolkit.wt.app.test.TestConfig;
import io.sitoolkit.wt.app.test.TestRunner;
import io.sitoolkit.wt.domain.debug.DebugSupport;
import io.sitoolkit.wt.gui.domain.test.TestRunParams;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateModel;
import io.sitoolkit.wt.util.infra.concurrent.ExecutorContainer;

public class TestService {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(TestService.class);

    private TestRunner runner = new TestRunner();

    private Map<String, ConfigurableApplicationContext> ctxMap = new HashMap<>();

    private TemplateEngine templateEngine;
    
    public TestService() {
        try (AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext(
                TestConfig.class)) {
            templateEngine = appCtx.getBean(TemplateEngine.class);
        }
    }
    
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

        PropertyManager runtimePm = appCtx.getBean(PropertyManager.class);

        if (runtimePm.isDebug() && getDebugSupport(sessionId).isPaused()) {
            getDebugSupport(sessionId).exit();
        } else {
            appCtx.close();
        }

        ctxMap.remove(sessionId);
    }

    public void destroy() {
        ctxMap.values().stream().forEach(ConfigurableApplicationContext::close);
    }

    private DebugSupport getDebugSupport(String sessionId) {
        ConfigurableApplicationContext appCtx = ctxMap.get(sessionId);
        return appCtx.getBean(DebugSupport.class);
    }

    public void createNewScript(File destFile) {

        String destFileBase = FilenameUtils.getBaseName(destFile.getName());
        String destFileExt = FilenameUtils.getExtension(destFile.getName());

        TemplateModel model = new TemplateModel();
        model.setTemplate("EmptyTestScript.vm");
        model.setOutDir(destFile.getParent());
        model.setFileBase(destFileBase);
        model.setFileExt(destFileExt);
        
        Properties properties = new Properties();
        properties.putAll(MessageManager.getMessageMap("testScript-"));
        model.setProperties(properties);

        templateEngine.write(model);
    }

}
