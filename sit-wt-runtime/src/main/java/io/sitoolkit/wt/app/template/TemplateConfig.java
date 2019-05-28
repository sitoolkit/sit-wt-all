package io.sitoolkit.wt.app.template;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.sitoolkit.wt.infra.template.MergedFileGenerator;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

@Configuration
public class TemplateConfig {

  @Bean
  public MergedFileGenerator mergedFileGenerator() {
    return new MergedFileGenerator();
  }

  @Bean
  public TemplateEngine templateEngine() {
    return new TemplateEngineVelocityImpl();
  }

}
