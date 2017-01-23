package org.sitoolkit.wt.app.test;

import org.sitoolkit.wt.app.config.BaseConfig;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@Import(BaseConfig.class)
public class TestCaseReaderConfig {

    public TestCaseReaderConfig() {
    }

    @Bean
    @Scope("prototype")
    public TestScript testScript() {
        return new TestScript();
    }

}
