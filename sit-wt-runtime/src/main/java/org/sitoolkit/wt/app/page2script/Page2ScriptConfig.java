package org.sitoolkit.wt.app.page2script;

import java.util.Arrays;

import org.sitoolkit.wt.app.config.BaseConfig;
import org.sitoolkit.wt.app.config.WebDriverConfig;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageListener;
import org.sitoolkit.wt.domain.pageload.PageLoader;
import org.sitoolkit.wt.domain.pageload.selenium.AnchorTagLoader;
import org.sitoolkit.wt.domain.pageload.selenium.InputTagLoader;
import org.sitoolkit.wt.domain.pageload.selenium.RadioCheckLoader;
import org.sitoolkit.wt.domain.pageload.selenium.SelectTagLoader;
import org.sitoolkit.wt.domain.pageload.selenium.SeleniumPageLietener;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@Import({ WebDriverConfig.class, BaseConfig.class })
public class Page2ScriptConfig {

    @Bean
    public Page2Script getTestScriptGenerator(TestScriptDao dao, PageListener listener,
            PageLoader... loaders) {
        Page2Script gen = new Page2Script();

        gen.setDao(dao);
        gen.setLoaders(Arrays.asList(loaders));
        gen.setListener(listener);

        return gen;
    }

    @Bean
    public RadioCheckLoader getRadioCheckLoader() {
        return new RadioCheckLoader();
    }

    @Bean
    public InputTagLoader getInputTagLoader() {
        return new InputTagLoader();
    }

    @Bean
    public SelectTagLoader getSelectTagLoader() {
        return new SelectTagLoader();
    }

    @Bean
    public AnchorTagLoader geAnchorTagLoader() {
        return new AnchorTagLoader();
    }

    @Bean
    public SeleniumPageLietener getListener() {
        return new SeleniumPageLietener();
    }

    @Bean
    @Scope("prototype")
    public PageContext getPageContext() {
        return new PageContext();
    }
}
