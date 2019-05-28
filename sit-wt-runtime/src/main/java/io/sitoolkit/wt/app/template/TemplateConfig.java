package io.sitoolkit.wt.app.template;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.sitoolkit.wt.infra.template.LocalizedFileGenerator;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

@Configuration
public class TemplateConfig {

  @Bean
  public LocalizedFileGenerator LocalizedFileGenerator() {
    return new LocalizedFileGenerator();
  }

  @Bean
  public TemplateEngine templateEngine() {
    return new TemplateEngineVelocityImpl();
  }

}
