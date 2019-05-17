package io.sitoolkit.wt.app.compareevidence;

import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.sitoolkit.wt.app.config.BaseConfig;
import io.sitoolkit.wt.app.evidence.EvidenceReportEditor;
import io.sitoolkit.wt.domain.evidence.DiffEvidence;
import io.sitoolkit.wt.domain.evidence.ReportOpener;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;

@Configuration
@Import(BaseConfig.class)
public class DiffEvidenceGeneratorConfig {

  @Resource
  @Bean
  public DiffEvidenceGenerator DiffEvidenceGenerator(DiffEvidence diffEvidence,
      TemplateEngine templateEngine) {
    DiffEvidenceGenerator generator = new DiffEvidenceGenerator();
    generator.setCompareEvidence(diffEvidence);
    generator.setTemplateEngine(templateEngine);
    return generator;
  }

  @Bean
  public DiffEvidence diffEvidence() {
    return new DiffEvidence();
  }

  @Bean
  public TemplateEngine templateEngine() {
    return new TemplateEngineVelocityImpl();
  }

  @Bean
  public MaskScreenshotGenerator maskScreenshotGenerator() {
    return new MaskScreenshotGenerator();
  }

  @Bean
  public MaskEvidenceGenerator maskEvidenceGenerator() {
    return new MaskEvidenceGenerator();
  }

  @Bean
  public EvidenceReportEditor evidenceReportEditor() {
    return new EvidenceReportEditor();
  }

  @Bean
  public ReportOpener reportOpener() {
    return new ReportOpener();
  }
}
