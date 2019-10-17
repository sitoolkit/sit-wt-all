package io.sitoolkit.wt.app.httpserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.sitoolkit.wt.domain.httpserver.ShutdownRequestHandler;
import io.sitoolkit.wt.domain.httpserver.SitHttpHandler;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

@Configuration
public class SitHttpServerConfig {

  @Bean
  public TemplateEngine templateEngine() {
    return new TemplateEngineVelocityImpl();
  }

  @Bean
  public SitHttpHandler sitHttpHandler() {
    return new SitHttpHandler();
  }

  @Bean
  public ShutdownRequestHandler shutdownRequestHandler() {
    return new ShutdownRequestHandler();
  }
}
