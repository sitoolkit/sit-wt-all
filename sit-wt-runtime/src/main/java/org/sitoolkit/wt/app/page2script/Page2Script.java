package org.sitoolkit.wt.app.page2script;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.app.config.ExtConfig;
import org.sitoolkit.wt.domain.debug.TestScriptGenerateTool;
import org.sitoolkit.wt.domain.pageload.PageContext;
import org.sitoolkit.wt.domain.pageload.PageListener;
import org.sitoolkit.wt.domain.pageload.PageLoader;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Page2Script implements TestScriptGenerateTool, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(Page2Script.class);

    private static final String MSG = "テストスクリプトに出力するページでEnterキーをタイプしてください。\n"
            + "終了する場合はqを入力しEnterキーをタイプしてください。";

    private List<PageLoader> loaders;

    private PageListener listener;

    private TestScriptDao dao;

    private ApplicationContext appCtx;

    private String outputDir = "pageobj";

    private boolean openScript = true;

    public static void main(String[] args) {
        System.exit(staticStart(true));
    }

    public static int staticStart(boolean openScript) {
        ConfigurableApplicationContext appCtx = new AnnotationConfigApplicationContext(
                Page2ScriptConfig.class, ExtConfig.class);

        Page2Script generator = appCtx.getBean(Page2Script.class);
        generator.setOpenScript(openScript);
        int ret = generator.start();
        appCtx.close();
        return ret;
    }

    public int start() {
        try {
            listener.setUp();

            Scanner scan = new Scanner(System.in);

            LOG.info("ブラウザが起動したらブラウザを操作してください。");
            LOG.info(MSG);

            while (!"q".equalsIgnoreCase(scan.nextLine())) {
                generateFromPage();
                LOG.info(MSG);
            }

            scan.close();

        } catch (Exception e) {
            LOG.error("予期しないエラーが発生しました。", e);
            return -1;

        } finally {
            listener.tearDown();
        }

        return 0;
    }

    public void generateFromPage() {
        PageContext pageCtx = appCtx.getBean(PageContext.class);

        if (listener != null) {
            listener.setUpPage(pageCtx);
        }

        LOG.info("ページの読み込みを開始します。{} {}", pageCtx.getTitle(), pageCtx.getUrl());

        for (PageLoader loader : loaders) {
            LOG.info("{}を実行します。", loader.getClass().getName());
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
        fileName = fileName + ".xlsx";
        String filePath = FilenameUtils.concat(outputDir, fileName);

        dao.write(filePath, pageCtx.asList());

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
}
