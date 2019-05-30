package io.sitoolkit.wt.app.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.sitoolkit.wt.app.template.TemplateConfig;

@Configuration
@Import(TemplateConfig.class)
public class SampleManagerConfig {

  @Bean
  public SampleManager sampleManager() {
    return new SampleManager();
  }

}
