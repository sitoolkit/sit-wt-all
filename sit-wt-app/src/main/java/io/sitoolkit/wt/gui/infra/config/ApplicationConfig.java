package io.sitoolkit.wt.gui.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.sitoolkit.wt.app.compareevidence.DiffEvidenceGeneratorConfig;
import io.sitoolkit.wt.app.config.ExtConfig;
import io.sitoolkit.wt.app.config.RuntimeConfig;
import io.sitoolkit.wt.app.page2script.Page2ScriptConfig;
import io.sitoolkit.wt.app.sample.SampleManagerConfig;
import io.sitoolkit.wt.app.test.TestCaseReader;
import io.sitoolkit.wt.app.test.TestCaseReaderConfig;
import io.sitoolkit.wt.app.test.TestScriptConfig;
import io.sitoolkit.wt.gui.app.diffevidence.DiffEvidenceService;
import io.sitoolkit.wt.gui.app.sample.SampleService;
import io.sitoolkit.wt.gui.app.script.ScriptService;
import io.sitoolkit.wt.gui.pres.AppController;
import io.sitoolkit.wt.gui.pres.DiffEvidenceToolbarController;
import io.sitoolkit.wt.gui.pres.FileTreeController;
import io.sitoolkit.wt.gui.pres.MenuBarController;
import io.sitoolkit.wt.gui.pres.SampleToolbarController;
import io.sitoolkit.wt.gui.pres.TestToolbarController;

@Configuration
@Import({SampleManagerConfig.class, DiffEvidenceGeneratorConfig.class, TestScriptConfig.class,
    Page2ScriptConfig.class, ExtConfig.class, TestCaseReaderConfig.class, RuntimeConfig.class})
public class ApplicationConfig {

  @Bean
  public AppController appController() {
    return new AppController();
  }

  @Bean
  public FileTreeController fileTreeController() {
    return new FileTreeController();
  }

  @Bean
  public MenuBarController menuBarController() {
    return new MenuBarController();
  }

  @Bean
  public TestToolbarController testToolbarController() {
    return new TestToolbarController();
  }

  @Bean
  public DiffEvidenceToolbarController diffEvidenceToolbarController() {
    return new DiffEvidenceToolbarController();
  }

  @Bean
  public SampleToolbarController sampleToolbarController() {
    return new SampleToolbarController();
  }

  @Bean
  public SampleService sampleService() {
    return new SampleService();
  }

  @Bean
  public DiffEvidenceService diffEvidenceService() {
    return new DiffEvidenceService();
  }

  @Bean
  public ScriptService ScriptService() {
    return new ScriptService();
  }

  @Bean
  public TestCaseReader TestCaseReader() {
    return new TestCaseReader();
  }
}
