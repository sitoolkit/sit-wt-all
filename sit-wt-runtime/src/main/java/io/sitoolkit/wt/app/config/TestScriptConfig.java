package io.sitoolkit.wt.app.config;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.domain.operation.OperationConverter;
import io.sitoolkit.wt.domain.operation.OperationResult;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.ELSupport;

@Configuration
public class TestScriptConfig {

    @Bean
    @Scope("prototype")
    public TestScript testScript() {
        return new TestScript();
    }

    @Bean
    @Scope("prototype")
    public TestStep testStep() {
        return new TestStep();
    }

    @Bean
    public ELSupport elSupport(TestContext testContext) {
        return new ELSupport(testContext);
    }

    @Bean
    @Scope("prototype")
    public Locator locator() {
        return new Locator();
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "thread")
    public TestContext testContext() {
        return new TestContext();
    }

    @Bean
    @Primary
    public OperationConverter getOperationConverter() {
        return new OperationConverter() {

            @Override
            public Optional<Operation> convert(String name) {
                return Optional.of(new Operation() {
                    @Override
                    public OperationResult operate(TestStep testStep) {
                        return null;
                    }
                });
            }

            @Override
            public List<String> getOperationNames() {
                return null;
            }

        };
    }

}
