package io.sitoolkit.wt.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import io.sitoolkit.wt.app.page2script.Page2ScriptConfig;
import io.sitoolkit.wt.domain.debug.DebugSupport;
import io.sitoolkit.wt.domain.debug.LocatorChecker;
import io.sitoolkit.wt.domain.debug.selenium.SeleniumLocatorChecker;
import io.sitoolkit.wt.domain.evidence.DialogScreenshotSupport;
import io.sitoolkit.wt.domain.evidence.Evidence;
import io.sitoolkit.wt.domain.evidence.EvidenceManager;
import io.sitoolkit.wt.domain.evidence.Screenshot;
import io.sitoolkit.wt.domain.evidence.ScreenshotTaker;
import io.sitoolkit.wt.domain.evidence.appium.HybridScreenshotTaker;
import io.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport2;
import io.sitoolkit.wt.domain.evidence.selenium.SeleniumDialogScreenshotSupport;
import io.sitoolkit.wt.domain.evidence.selenium.SeleniumScreenshotTaker;
import io.sitoolkit.wt.domain.operation.DbVerifyLog;
import io.sitoolkit.wt.domain.operation.HtmlTable;
import io.sitoolkit.wt.domain.tester.OperationSupport;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.domain.tester.Tester;
import io.sitoolkit.wt.domain.tester.selenium.DbVerifyOperationSupport;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestScriptCatalog;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.ApplicationContextHelper;
import io.sitoolkit.wt.infra.ELSupport;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;
import io.sitoolkit.wt.util.infra.util.SystemUtils;

@Configuration
@Import({ BaseConfig.class, WebDriverConfig.class, Page2ScriptConfig.class, DbConfig.class })
@ComponentScan("io.sitoolkit.wt.domain.operation")
public class RuntimeConfig {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(RuntimeConfig.class);

    public RuntimeConfig() {
        LOG.info("environment.info", SystemUtils.getEnvironmentInfo());
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.DEFAULT, scopeName = "thread")
    public Tester tester() {
        return new Tester();
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "thread")
    public ElementPositionSupport2 elementPositionSupport() {
        return new ElementPositionSupport2();
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "thread")
    public TestContext testContext() {
        return new TestContext();
    }

    @Bean
    public EvidenceManager em() {
        return new EvidenceManager();
    }

    @Bean
    public DebugSupport debugSupport() {
        return new DebugSupport();
    }

    @Bean
    public LocatorChecker locatorChecker() {
        return new SeleniumLocatorChecker();
    }

    @Bean
    public DialogScreenshotSupport dialogScreenshotSupport() {
        return new SeleniumDialogScreenshotSupport();
    }

    @Bean
    public TestScriptCatalog TestScriptCatalog() {
        return new TestScriptCatalog();
    }

    @Bean
    @Scope("prototype")
    public TestScript testScript() {
        return new TestScript();
    }

    @Bean
    @Scope("prototype")
    public TestStep testStep() {
        return new TestStep();
    }

    @Bean
    public ELSupport elSupport(TestContext testContext) {
        return new ELSupport(testContext);
    }

    @Bean
    @Scope("prototype")
    public Locator locator() {
        return new Locator();
    }

    @Bean
    public ScreenshotTaker screenshotTaker(PropertyManager pm) {

        if ("hybrid".equals(pm.getScreenthotMode())) {
            return new HybridScreenshotTaker();
        }

        SeleniumScreenshotTaker taker = new SeleniumScreenshotTaker();
        taker.setResizeWindow(pm.isResizeWindow());

        return taker;
    }

    @Bean
    @Scope("prototype")
    public Screenshot screenshot(PropertyManager pm) {
        Screenshot screenshot = new Screenshot();

        screenshot.setResize(pm.isScreenshotResize());
        screenshot.setScreenshotPaddingWidth(pm.getScreenshotPaddingWidth());
        screenshot.setScreenshotPaddingHeight(pm.getScreenshotPaddingHeight());

        return screenshot;
    }

    @Bean
    @Scope("prototype")
    public Evidence evidence() {
        return new Evidence();
    }

    @Bean
    public TemplateEngine templateEngine() {
        return new TemplateEngineVelocityImpl();
    }

    @Bean
    public HtmlTable htmlTable() {
        return new HtmlTable();
    }

    @Bean
    public DbVerifyLog dbVerifyObject() {
        return new DbVerifyLog();
    }

    @Bean
    public OperationSupport operationSupport() {
        return new DbVerifyOperationSupport();
    }

    @Bean
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

}
