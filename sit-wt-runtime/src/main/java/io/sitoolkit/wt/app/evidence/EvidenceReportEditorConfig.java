package io.sitoolkit.wt.app.evidence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.sitoolkit.wt.app.template.TemplateConfig;

@Configuration
@Import({TemplateConfig.class})
public class EvidenceReportEditorConfig {

  @Bean
  public EvidenceReportEditor evidenceReportEditor() {
    return new EvidenceReportEditor();
  }

}
