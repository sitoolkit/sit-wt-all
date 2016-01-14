package org.sitoolkit.wt.app.config;

import org.sitoolkit.util.tabledata.BeanFactory;
import org.sitoolkit.util.tabledata.FileInputSourceWatcher;
import org.sitoolkit.util.tabledata.FileOverwriteChecker;
import org.sitoolkit.util.tabledata.InputSourceWatcher;
import org.sitoolkit.util.tabledata.TableDataDao;
import org.sitoolkit.util.tabledata.TableDataMapper;
import org.sitoolkit.util.tabledata.excel.TableDataDaoExcelImpl;
import org.sitoolkit.wt.domain.operation.Operation;
import org.sitoolkit.wt.domain.testscript.OperationConverter;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.SpringBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PropertyManager.class)
public class BaseConfig {

    @Bean
    public FileOverwriteChecker getFileOverwriteChecker() {
        return new FileOverwriteChecker();
    }

    @Bean
    public FileInputSourceWatcher getFileInputSourceWatcher() {
        return new FileInputSourceWatcher();
    }

    @Bean
    public TableDataDao tableDataDao(FileOverwriteChecker fileOverwriteChecker,
            InputSourceWatcher watcher) {

        TableDataDaoExcelImpl dao = new TableDataDaoExcelImpl();
        dao.setFileOverwriteChecker(fileOverwriteChecker);
        dao.setInputSourceWatcher(watcher);

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
