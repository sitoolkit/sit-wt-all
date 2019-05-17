package io.sitoolkit.wt.app.page2script;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import io.sitoolkit.wt.app.config.BaseConfig;
import io.sitoolkit.wt.app.config.WebDriverConfig;
import io.sitoolkit.wt.domain.pageload.PageContext;
import io.sitoolkit.wt.domain.pageload.PageListener;
import io.sitoolkit.wt.domain.pageload.PageLoader;
import io.sitoolkit.wt.domain.pageload.selenium.AnchorTagLoader;
import io.sitoolkit.wt.domain.pageload.selenium.InputTagLoader;
import io.sitoolkit.wt.domain.pageload.selenium.RadioCheckLoader;
import io.sitoolkit.wt.domain.pageload.selenium.SelectTagLoader;
import io.sitoolkit.wt.domain.pageload.selenium.SeleniumPageLietener;
import io.sitoolkit.wt.domain.pageload.selenium.TextareaTagLoader;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.infra.PropertyManager;

@Configuration
@Import({WebDriverConfig.class, BaseConfig.class})
public class Page2ScriptConfig {

  @Bean
  public Page2Script getTestScriptGenerator(TestScriptDao dao, PageListener listener,
      PropertyManager pm, PageLoader... loaders) {
    Page2Script page2script = new Page2Script();

    page2script.setDao(dao);
    page2script.setLoaders(Arrays.asList(loaders));
    page2script.setListener(listener);
    String projectDir = System.getProperty("sitwt.projectDirectory");
    String pageScriptDir = (StringUtils.isEmpty(projectDir)) ? pm.getPageScriptDir()
        : projectDir + "/" + pm.getPageScriptDir();
    page2script.setOutputDir(pageScriptDir);
    page2script.setCli(pm.isCli());

    return page2script;
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
  public AnchorTagLoader getAnchorTagLoader() {
    return new AnchorTagLoader();
  }

  @Bean
  public TextareaTagLoader getTextareaTagLoader() {
    return new TextareaTagLoader();
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
