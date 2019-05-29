package io.sitoolkit.wt.gui.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.sitoolkit.wt.app.sample.SampleManager;
import io.sitoolkit.wt.app.template.TemplateConfig;
import io.sitoolkit.wt.gui.app.sample.SampleService;
import io.sitoolkit.wt.gui.pres.AppController;
import io.sitoolkit.wt.gui.pres.DiffEvidenceToolbarController;
import io.sitoolkit.wt.gui.pres.FileTreeController;
import io.sitoolkit.wt.gui.pres.MenuBarController;
import io.sitoolkit.wt.gui.pres.SampleToolbarController;
import io.sitoolkit.wt.gui.pres.TestToolbarController;

@Configuration
@Import({TemplateConfig.class})
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
  public SampleManager sampleManager() {
    return new SampleManager();
  }

  @Bean
  public SampleService sampleService() {
    return new SampleService();
  }

}
