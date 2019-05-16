package io.sitoolkit.wt.app.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import io.sitoolkit.wt.app.config.BaseConfig;
import io.sitoolkit.wt.domain.testscript.TestScript;

@Configuration
@Import(BaseConfig.class)
public class TestCaseReaderConfig {

  public TestCaseReaderConfig() {}

  @Bean
  @Scope("prototype")
  public TestScript testScript() {
    return new TestScript();
  }

}
