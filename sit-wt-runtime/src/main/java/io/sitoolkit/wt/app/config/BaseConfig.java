package io.sitoolkit.wt.app.config;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.SimpleThreadScope;

import io.sitoolkit.util.tabledata.BeanFactory;
import io.sitoolkit.util.tabledata.FileInputSourceWatcher;
import io.sitoolkit.util.tabledata.FileOverwriteChecker;
import io.sitoolkit.util.tabledata.InputSourceWatcher;
import io.sitoolkit.util.tabledata.TableDataMapper;
import io.sitoolkit.util.tabledata.csv.TableDataDaoCsvImpl;
import io.sitoolkit.util.tabledata.excel.TableDataDaoExcelImpl;
import io.sitoolkit.wt.domain.operation.Operation;
import io.sitoolkit.wt.domain.testscript.OperationConverter;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.SpringBeanFactory;
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
    public FileOverwriteChecker fileOverwriteChecker() {
        return new FileOverwriteChecker();
    }

    @Bean
    public OverwriteChecker overwriteChecker() {
        return new OverwriteChecker();
    }

    @Bean
    public FileInputSourceWatcher getFileInputSourceWatcher() {
        return new FileInputSourceWatcher();
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
    public TableDataDaoExcelImpl excelDao(FileOverwriteChecker fileOverwriteChecker,
            InputSourceWatcher watcher) {

        TableDataDaoExcelImpl dao = new TableDataDaoExcelImpl();
        dao.setFileOverwriteChecker(fileOverwriteChecker);
        dao.setInputSourceWatcher(watcher);

        return dao;
    }

    @Bean
    public TableDataDaoCsvImpl tableDataDaoCsvImpl(FileOverwriteChecker fileOverwriteChecker,
            InputSourceWatcher inputSourceWatcher) {

        TableDataDaoCsvImpl dao = new TableDataDaoCsvImpl();
        dao.setFileOverwriteChecker(fileOverwriteChecker);
        dao.setInputSourceWatcher(inputSourceWatcher);

        return dao;
    }

    @Bean
    public OperationConverter getOperationConverter() {
        return new OperationConverter();
    }

    @Bean
    public TableDataMapper tableDataMapper(OperationConverter converter, BeanFactory beanFactory) {
        TableDataMapper tdm = new TableDataMapper();
        tdm.getConverterMap().put(Operation.class, converter);
        tdm.setBeanFactory(beanFactory);
        return tdm;
    }

    @Bean
    public BeanFactory beanFactory() {
        return new SpringBeanFactory();
    }

    @Bean
    public TestScriptDao getTestScriptDao() {
        return new TestScriptDao();
    }
}
