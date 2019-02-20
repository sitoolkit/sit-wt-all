package io.sitoolkit.wt.app.config;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.SimpleThreadScope;

import io.sitoolkit.wt.domain.operation.OperationConverter;
import io.sitoolkit.wt.domain.operation.selenium.SeleniumOperationConverter;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.csv.CsvFileReader;
import io.sitoolkit.wt.infra.csv.CsvFileWriter;
import io.sitoolkit.wt.util.infra.util.OverwriteChecker;

@Configuration
@Import(PropertyManager.class)
public class BaseConfig {

    @Bean
    public static SimpleThreadScope threadScope() {
        return new SimpleThreadScope();
    }

    @Bean
    public static CustomScopeConfigurer customScopeConfigurer(SimpleThreadScope threadScope) {
        CustomScopeConfigurer csc = new CustomScopeConfigurer();

        csc.addScope("thread", threadScope);

        return csc;
    }

    @Bean
    public OverwriteChecker overwriteChecker() {
        return new OverwriteChecker();
    }

    @Bean
    public CsvFileReader getCsvFileReader() {
        return new CsvFileReader();
    }

    @Bean
    public CsvFileWriter getCsvFileWriter() {
        return new CsvFileWriter();
    }

    @Bean
    public OperationConverter getOperationConverter() {
        return new SeleniumOperationConverter();
    }

    @Bean
    public TestScriptDao getTestScriptDao() {
        return new TestScriptDao();
    }
}
