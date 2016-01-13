package org.sitoolkit.wt.domain.evidence;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.sitoolkit.wt.infra.TestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ResourceUtils;

public class EvidenceManager implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceManager.class);

    private List<LogRecord> records = new ArrayList<LogRecord>();

    /**
     * 操作ログのVelocityテンプレート
     */
    private String templatePath = "/opelog/opelog-template.vm";

    /**
     * 操作ログの表示に関連する資源
     */
    private String[] opelogResources = new String[] { "classpath:opelog/style.css",
            "classpath:opelog/jquery.js", "classpath:opelog/numbering.js" };

    /**
     * 操作ログの出力先ディレクトリ
     */
    private File opelogRootDir;
    /**
     * スクリーンショットの出力先ディレクトリ
     */
    private File imgDir;
    private String logFilePath = "target/sit-wt.log";
    private Screenshot screenshot;
    private Template tmpl;
    private ApplicationContext appCtx;

    @PostConstruct
    public void init() {
        opelogRootDir = new File("target",
                "opelog_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        opelogRootDir.mkdirs();
        if (opelogRootDir.exists()) {
            LOG.info("操作ログ出力ディレクトリを作成しました。{}", opelogRootDir.getAbsolutePath());
        } else {
            throw new TestException("操作ログ出力ディレクトリの作成に失敗しました" + opelogRootDir.getAbsoluteFile());
        }

        imgDir = new File(opelogRootDir, "img");
        imgDir.mkdirs();
        if (!imgDir.exists()) {
            throw new TestException("スクリーンショット出力ディレクトリの作成に失敗しました" + imgDir.getAbsoluteFile());
        }
        try {
            Properties prop = PropertyUtils.load("/velocity.properties", false);
            Velocity.init(prop);
            tmpl = Velocity.getTemplate(templatePath);
            for (String opelogRes : opelogResources) {
                URL url = ResourceUtils.getURL(opelogRes);
                File dstFile = new File(opelogRootDir,
                        StringUtils.substringAfterLast(url.getPath(), "/"));
                FileUtils.copyURLToFile(url, dstFile);
            }
        } catch (IOException e) {
            throw new TestException(e);
        }

        screenshot = appCtx.getBean(Screenshot.class);
    }

    public void addElementPosition(ElementPosition pos) {
        screenshot.addElementPosition(pos);
    }

    /**
     * 位置情報のリストを再作成します。
     */
    public void flushScreenshot() {
        if (screenshot.flush()) {
            screenshot = appCtx.getBean(Screenshot.class);
        }
    }

    public void addLogRecord(LogRecord log) {
        records.add(log);
    }

    public void addScreenshotLogRecord(LogRecord log, File screenshotFile, String scriptName,
            String caseNo, String testStepNo, String itemName, String timing, String resize) {

        screenshot.setFile(imgDir, screenshotFile, scriptName, caseNo, testStepNo, itemName,
                timing);
        screenshot.setBasedir(opelogRootDir);

        // TODO スクリーンショットを個別にリサイズするかしないかの設定方法を検討
        if (screenshot.isResize()) {
            if (StringUtils.contains(resize, "全")) {
                screenshot.setResize(false);
            }
        } else {
            if (StringUtils.contains(resize, "縮")) {
                screenshot.setResize(true);
            }
        }

        log.setScreenshot(screenshot);
        addLogRecord(log);

    }

    public void flush(String scriptName, String caseNo, String evidence) {
        File htmlFile = new File(opelogRootDir, opelogFileName(scriptName, caseNo));

        if (htmlFile.exists()) {
            htmlFile = new File(htmlFile.getParent(),
                    System.currentTimeMillis() + "_" + htmlFile.getName());
        }

        LOG.info("操作ログを出力します {}", htmlFile.getAbsolutePath());

        try {
            FileUtils.write(htmlFile, evidence, "UTF-8");
        } catch (Exception e) {
            throw new TestException("操作ログの出力に失敗しました", e);
        } finally {
            records.clear();
            flushScreenshot();
        }

    }

    private String opelogFileName(String scriptName, String caseNo) {

        String resultHtml = ".html";
        if (hasError()) {
            resultHtml = "_NG.html";
        }

        return StringUtils.join(new String[] { scriptName, caseNo }, "_") + resultHtml;

    }

    /**
     * 操作ログファイルに出力する文字列を構築します。
     *
     * @return 操作ログファイルに出力する文字列
     */
    public String build(String caseNo, String scriptName) {
        VelocityContext context = new VelocityContext();
        StringWriter writer = new StringWriter();
        context.put("records", records);
        context.put("caseNo", caseNo);
        context.put("testScriptName", scriptName);
        context.put("result", (hasError()) ? "NG" : "");
        tmpl.merge(context, writer);
        return writer.toString();
    }

    public void moveLogFile() {
        try {
            File logFile = new File(logFilePath);
            FileUtils.copyFileToDirectory(logFile, opelogRootDir, true);
            logFile.deleteOnExit();
        } catch (IOException e) {
            throw new TestException(e);
        }
    }

    private boolean hasError() {
        for (LogRecord logRecord : records) {
            if (LogLevelVo.ERROR.equals(logRecord.getLogLevel())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        appCtx = arg0;
    }

}
