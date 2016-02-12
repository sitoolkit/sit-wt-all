package org.sitoolkit.wt.app.config;

import org.sitoolkit.wt.app.page2script.Page2ScriptImportConfig;
import org.sitoolkit.wt.domain.debug.DebugSupport;
import org.sitoolkit.wt.domain.evidence.DialogScreenshotSupport;
import org.sitoolkit.wt.domain.evidence.Evidence;
import org.sitoolkit.wt.domain.evidence.EvidenceManager;
import org.sitoolkit.wt.domain.evidence.Screenshot;
import org.sitoolkit.wt.domain.evidence.ScreenshotTaker;
import org.sitoolkit.wt.domain.evidence.appium.HybridScreenshotTaker;
import org.sitoolkit.wt.domain.evidence.selenium.ElementPositionSupport2;
import org.sitoolkit.wt.domain.evidence.selenium.SeleniumDialogScreenshotSupport;
import org.sitoolkit.wt.domain.evidence.selenium.SeleniumScreenshotTaker;
import org.sitoolkit.wt.domain.tester.ELSupport;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.tester.Tester;
import org.sitoolkit.wt.domain.tester.selenium.SeleniumTester;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.PropertyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@Import({ BaseConfig.class, WebDriverConfig.class, Page2ScriptImportConfig.class })
@ComponentScan("org.sitoolkit.wt.domain.operation")
public class RuntimeConfig {

    @Bean(name = "tester")
    public Tester getTester(ScreenshotTaker screenshotTaker) {
        SeleniumTester tester = new SeleniumTester();

        return tester;
    }

    @Bean
    public ElementPositionSupport2 elementPositionSupport() {
        return new ElementPositionSupport2();
    }

    @Bean
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
    public DialogScreenshotSupport dialogScreenshotSupport() {
        return new SeleniumDialogScreenshotSupport();
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
    public ELSupport elSupport() {
        return new ELSupport();
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
}
