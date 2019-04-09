package io.sitoolkit.wt.app.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

@Configuration
public class TestConfig {

    @Bean
    public TemplateEngine templateEngine() {
        return new TemplateEngineVelocityImpl();
    }

}
