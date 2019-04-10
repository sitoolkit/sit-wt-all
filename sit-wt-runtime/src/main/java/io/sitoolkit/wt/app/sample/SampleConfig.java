package io.sitoolkit.wt.app.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

@Configuration
public class SampleConfig {

    @Bean
    public TemplateEngine templateEngine() {
        return new TemplateEngineVelocityImpl();
    }

}
