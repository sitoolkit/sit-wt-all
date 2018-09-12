package io.sitoolkit.wt.app.pagespec2script;

import javax.annotation.Resource;

import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import io.sitoolkit.util.tabledata.TableDataDao;
import io.sitoolkit.wt.app.config.BaseConfig;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.infra.PropertyUtils;

@Configuration
@Import(BaseConfig.class)
public class PageSpec2ScriptConfig {

    @Resource
    @Bean
    public PageSpec2Script getPageSpecConverter(TestScriptDao tsDao, TableDataDao excelDao) {
        PageSpec2Script conv = new PageSpec2Script();
        conv.setFileFilter(new PrefixFileFilter("画面定義書_"));
        conv.setDao(tsDao);
        conv.setTableDataDao(excelDao);
        conv.setPageSpecConverterMap(PropertyUtils.loadAsMap("/item2operation", false));

        return conv;
    }

    @Bean(name = "pageItemSpec")
    @Scope("prototype")
    public PageItemSpec getPateItemSpec() {
        return new PageItemSpec();
    }
}
