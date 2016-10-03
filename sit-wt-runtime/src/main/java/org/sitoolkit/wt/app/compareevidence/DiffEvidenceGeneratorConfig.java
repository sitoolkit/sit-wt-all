package org.sitoolkit.wt.app.compareevidence;

import javax.annotation.Resource;

import org.sitoolkit.wt.app.config.BaseConfig;
import org.sitoolkit.wt.domain.evidence.DiffEvidence;
import org.sitoolkit.wt.infra.template.TemplateEngine;
import org.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BaseConfig.class)
public class DiffEvidenceGeneratorConfig {

    @Resource
    @Bean
    DiffEvidenceGenerator DiffEvidenceGenerator(DiffEvidence diffEvidence,
            TemplateEngine templateEngine) {
        DiffEvidenceGenerator generator = new DiffEvidenceGenerator();
        generator.setCompareEvidence(diffEvidence);
        generator.setTemplateEngine(templateEngine);
        return generator;
    }

    @Bean
    DiffEvidence diffEvidence() {
        return new DiffEvidence();
    }

    @Bean
    TemplateEngine templateEngine() {
        return new TemplateEngineVelocityImpl();
    }

    @Bean
    MaskScreenshotGenerator maskScreenshotGenerator() {
        return new MaskScreenshotGenerator();
    }

}
