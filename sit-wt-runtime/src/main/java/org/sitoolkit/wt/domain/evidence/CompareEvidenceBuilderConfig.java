package org.sitoolkit.wt.domain.evidence;

import javax.annotation.Resource;

import org.sitoolkit.wt.app.config.BaseConfig;
import org.sitoolkit.wt.infra.template.TemplateEngine;
import org.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BaseConfig.class)
public class CompareEvidenceBuilderConfig {

    @Resource
    @Bean
    public DiffEvidenceGenerator compareEvidenceBuilder(TemplateEngine templateEngine,
            DiffEvidence compareEvidence) {
        DiffEvidenceGenerator builder = new DiffEvidenceGenerator();
        builder.setTemplateEngine(templateEngine);
        builder.setCompareEvidence(compareEvidence);
        return builder;
    }

    @Bean
    public TemplateEngine templateEngine() {
        TemplateEngine engine = new TemplateEngineVelocityImpl();
        return engine;
    }

    @Bean
    public DiffEvidence compareEvidence() {
        return new DiffEvidence();
    }
}
