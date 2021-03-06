package io.sitoolkit.wt.app.page2script;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import javax.annotation.Resource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import io.sitoolkit.wt.app.config.ExtConfig;
import io.sitoolkit.wt.domain.debug.TestScriptGenerateTool;
import io.sitoolkit.wt.domain.pageload.PageContext;
import io.sitoolkit.wt.domain.pageload.PageListener;
import io.sitoolkit.wt.domain.pageload.PageLoader;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class Page2Script implements TestScriptGenerateTool, ApplicationContextAware {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(Page2Script.class);

  private static final String MSG =
      "テストスクリプトに出力するページでEnterキーをタイプしてください。\n" + "終了する場合はqを入力しEnterキーをタイプしてください。";

  private List<PageLoader> loaders;

  private PageListener listener;

  private TestScriptDao dao;

  private ApplicationContext appCtx;

  private String outputDir;

  private boolean openScript = true;

  private boolean isCli = true;

  private Path createFile;

  @Resource
  private PropertyManager pm;

  public static void main(String[] args) {
    System.exit(staticStart(true));
  }

  public static int staticStart(boolean openScript) {
    ConfigurableApplicationContext appCtx =
        new AnnotationConfigApplicationContext(Page2ScriptConfig.class, ExtConfig.class);

    Page2Script page2script = appCtx.getBean(Page2Script.class);
    page2script.setOpenScript(openScript);
    int ret = page2script.start();
    appCtx.close();
    return ret;
  }

  public int start() {
    try {
      listener.setUp();

      Scanner scan = new Scanner(System.in);

      LOG.info("browser.start.operation");
      if (isCli) {
        LOG.info("msg", MSG);
      }

      while (!"q".equalsIgnoreCase(scan.nextLine())) {
        generateFromPage();

        if (isCli) {
          LOG.info("msg", MSG);
        }
      }

      scan.close();

    } catch (Exception e) {
      LOG.error("unexpected.error", e);
      return -1;

    } finally {
      listener.tearDown();
    }

    return 0;
  }

  public void openBrowser(String baseUrl, String driverType) {
    pm.setBaseUrl(baseUrl);
    pm.setDriverType(driverType);
    listener.setUp();
    LOG.info("browser.start.operation");
  }

  public void generateFromPage() {
    PageContext pageCtx = appCtx.getBean(PageContext.class);

    if (listener != null) {
      listener.setUpPage(pageCtx);
    }

    LOG.info("page.load.start", pageCtx.getTitle(), pageCtx.getUrl());

    for (PageLoader loader : loaders) {
      LOG.info("page.load.execute", loader.getClass().getName());
      loader.load(pageCtx);
    }

    if (listener != null) {
      listener.tearDownPage(pageCtx);
    }

    String fileName = pageCtx.getTitle();
    if (StringUtils.isEmpty(fileName)) {
      fileName = pageCtx.getUrl();
      fileName = StringUtils.substringAfterLast(fileName, "/");
      fileName = StringUtils.substringBefore(fileName, "?");
    }
    fileName = fileName + ".csv";
    String filePath = FilenameUtils.concat(outputDir, fileName);

    filePath = dao.write(filePath, pageCtx.asList(), false);
    setCreateFile(Paths.get(filePath));

    if (isOpenScript()) {
      try {
        Desktop.getDesktop().open(new File(filePath));
      } catch (IOException e) {
        // NOP
      }
    }

  }

  public List<PageLoader> getLoaders() {
    return loaders;
  }

  public void setLoaders(List<PageLoader> loaders) {
    this.loaders = loaders;
  }

  public PageListener getListener() {
    return listener;
  }

  public void setListener(PageListener listener) {
    this.listener = listener;
  }

  public TestScriptDao getDao() {
    return dao;
  }

  public void setDao(TestScriptDao dao) {
    this.dao = dao;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.appCtx = applicationContext;
  }

  public String getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  public boolean isOpenScript() {
    return openScript;
  }

  public void setOpenScript(boolean openScript) {
    this.openScript = openScript;
  }

  public void setCli(boolean isCli) {
    this.isCli = isCli;
  }

  public Path getCreateFile() {
    return this.createFile;
  }

  public void setCreateFile(Path createFile) {
    this.createFile = createFile;
  }

}
