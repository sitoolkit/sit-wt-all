package io.sitoolkit.wt.app.config;

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.PropertyUtils;

@Configuration
@EnableTransactionManagement
public class DbConfig {

    @Resource
    PropertyManager pm;

    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();

        Properties prop = PropertyUtils.load(pm.getConnectionProperties(), false);

        basicDataSource.setDriverClassName(prop.getProperty("jdbc.driver"));
        basicDataSource.setUrl(prop.getProperty("jdbc.url"));
        basicDataSource.setUsername(prop.getProperty("jdbc.username"));
        basicDataSource.setPassword(prop.getProperty("jdbc.password"));

        return basicDataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(BasicDataSource basicDataSource) {
        return new DataSourceTransactionManager(basicDataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

}
