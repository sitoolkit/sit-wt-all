package org.sitoolkit.wt.app.config;

import java.lang.reflect.Field;

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
import org.sitoolkit.wt.infra.VoidFileOverwriteChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PropertyManager.class)
public class BaseConfig {

    @Bean
    public VoidFileOverwriteChecker getFileOverwriteChecker() {
        return new VoidFileOverwriteChecker();
    }

    @Bean
    public FileInputSourceWatcher getFileInputSourceWatcher() {
        return new FileInputSourceWatcher();
    }

    @Bean
    public TableDataDao tableDataDao(FileOverwriteChecker fileOverwriteChecker,
            InputSourceWatcher watcher) {
        TableDataDaoExcelImpl dao = new TableDataDaoExcelImpl();

        // TODO sit-util-td 0.5までの暫定対応
        try {
            Field focfield = dao.getClass().getDeclaredField("fileOverwriteChecker");
            focfield.setAccessible(true);
            focfield.set(dao, fileOverwriteChecker);

            Field iswField = dao.getClass().getDeclaredField("watcher");
            iswField.setAccessible(true);
            iswField.set(dao, watcher);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
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
