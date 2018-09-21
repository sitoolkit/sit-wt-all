package io.sitoolkit.wt.app.config;

import org.apache.commons.beanutils.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import io.sitoolkit.util.tabledata.BeanFactory;
import io.sitoolkit.util.tabledata.TableDataMapper;
import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.domain.operation.OperationResult;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.OperationConverter;
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
    public TableDataMapper tableDataMapper(OperationConverter converter, BeanFactory beanFactory) {
        TableDataMapper tdm = new TableDataMapper();
        tdm.getConverterMap().put(Operation.class, new Converter() {

            @Override
            public <T> T convert(Class<T> type, Object value) {
                return (T) new Operation() {

                    @Override
                    public OperationResult operate(TestStep testStep) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };
            }
        });
        tdm.setBeanFactory(beanFactory);
        return tdm;
    }

    @Bean
    @Primary
    public OperationConverter getOperationConverter() {
        return new OperationConverter() {

            @Override
            public Object convert(Class type, Object o) {
                return new Operation() {

                    @Override
                    public OperationResult operate(TestStep testStep) {
                        return null;
                    }
                };
            }
        };
    }

}
