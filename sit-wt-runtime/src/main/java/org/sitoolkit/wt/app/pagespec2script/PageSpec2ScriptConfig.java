package org.sitoolkit.wt.app.pagespec2script;

import javax.annotation.Resource;

import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.sitoolkit.util.tabledata.TableDataDao;
import org.sitoolkit.wt.app.config.BaseConfig;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@Import(BaseConfig.class)
public class PageSpec2ScriptConfig {

    @Resource
    @Bean
    public PageSpec2Script getPageSpecConverter(TestScriptDao tsDao, TableDataDao tdDao) {
        PageSpec2Script conv = new PageSpec2Script();
        conv.setFileFilter(new PrefixFileFilter("画面定義書_"));
        conv.setDao(tsDao);
        conv.setTableDataDao(tdDao);
        conv.setPageSpecConverterMap(PropertyUtils.loadAsMap("/item2operation", false));

        return conv;
    }

    @Bean(name = "pageItemSpec")
    @Scope("prototype")
    public PageItemSpec getPateItemSpec() {
        return new PageItemSpec();
    }
}
