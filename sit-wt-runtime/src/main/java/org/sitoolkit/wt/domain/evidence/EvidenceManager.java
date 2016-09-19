package org.sitoolkit.wt.domain.evidence;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.sitoolkit.wt.infra.SitPathUtils;
import org.sitoolkit.wt.infra.TestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ResourceUtils;

public class EvidenceManager implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceManager.class);

    /**
     * エビデンスのVelocityテンプレート
     */
    private String templatePath = "/evidence/evidence-template.vm";

    /**
     * エビデンスの表示に関連する資源
     */
    private String[] evidenceResources = new String[] { "css/bootstrap.min.css", "css/style.css",
            "css/jquery-ui.min.css", "js/jquery.js", "js/numbering.js", "js/image.js",
            "js/jquery-ui.min.js" };

    /**
     * エビデンスの出力先ディレクトリ
     */
    private File evidenceDir;
    /**
     * スクリーンショットの出力先ディレクトリ
     */
    private File imgDir;
    private String logFilePath = "target/sit-wt.log";
    private Template tmpl;
    private ApplicationContext appCtx;

    @PostConstruct
    public void init() {
        evidenceDir = new File("target",
                "evidence_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        evidenceDir.mkdirs();
        if (evidenceDir.exists()) {
            LOG.info("エビデンス出力ディレクトリを作成しました。{}", evidenceDir.getAbsolutePath());
        } else {
            throw new TestException("エビデンス出力ディレクトリの作成に失敗しました" + evidenceDir.getAbsoluteFile());
        }

        imgDir = new File(evidenceDir, "img");
        imgDir.mkdirs();
        if (!imgDir.exists()) {
            throw new TestException("スクリーンショット出力ディレクトリの作成に失敗しました" + imgDir.getAbsoluteFile());
        }
        try {
            Properties prop = PropertyUtils.load("/velocity.properties", false);
            Velocity.init(prop);
            tmpl = Velocity.getTemplate(templatePath);
            for (String evidenceRes : evidenceResources) {
                URL url = ResourceUtils.getURL("classpath:evidence/" + evidenceRes);
                File dstFile = new File(evidenceDir, evidenceRes);
                FileUtils.copyURLToFile(url, dstFile);
            }
        } catch (IOException e) {
            throw new TestException(e);
        }
    }

    public Evidence createEvidence(String scriptName, String caseNo) {
        Evidence evidence = appCtx.getBean(Evidence.class);
        evidence.setScriptName(scriptName);
        evidence.setCaseNo(caseNo);
        return evidence;
    }

    public void moveScreenshot(Evidence evidence, String testStepNo, String itemName) {
        Screenshot screenshot = evidence.getCurrentScreenshot();
        File file = screenshot.getFile();
        if (file == null) {
            return;
        }

        String screenshotFileName = buildScreenshotFileName(evidence.getScriptName(),
                evidence.getCaseNo(), testStepNo, itemName, screenshot.getTiming().name());
        File dstFile = new File(imgDir, screenshotFileName);

        try {

            if (dstFile.exists()) {
                dstFile = new File(imgDir, dstFile.getName() + "_" + System.currentTimeMillis());
            }

            FileUtils.moveFile(file, dstFile);
            screenshot.setFile(dstFile);
            screenshot.setFilePath(SitPathUtils.relatvePath(evidenceDir, dstFile));

            LOG.info("スクリーンショットを取得しました {}", dstFile.getAbsolutePath());

        } catch (IOException e) {
            LOG.warn("スクリーンショットファイルの移動に失敗しました", e);
        }

    }

    private String buildScreenshotFileName(String scriptName, String caseNo, String testStepNo,
            String itemName, String timing) {

        return StringUtils.join(new String[] { scriptName, caseNo, testStepNo, itemName, timing },
                "_") + ".png";
    }

    /**
     * エビデンスをファイルに書き出します。
     *
     * @param evidence
     *            エビデンス
     */
    public void flushEvidence(Evidence evidence) {
        String html = build(evidence);

        File htmlFile = new File(evidenceDir, buildEvidenceFileName(evidence.getScriptName(),
                evidence.getCaseNo(), evidence.hasError()));

        if (htmlFile.exists()) {
            htmlFile = new File(htmlFile.getParent(),
                    System.currentTimeMillis() + "_" + htmlFile.getName());
        }

        LOG.info("エビデンスを出力します {}", htmlFile.getAbsolutePath());

        try {
            FileUtils.write(htmlFile, html, "UTF-8");
        } catch (Exception e) {
            throw new TestException("エビデンスの出力に失敗しました", e);
        }
    }

    private String buildEvidenceFileName(String scriptName, String caseNo, boolean hasError) {

        String resultHtml = ".html";
        if (hasError) {
            resultHtml = "_NG.html";
        }

        return StringUtils.join(new String[] { scriptName, caseNo }, "_") + resultHtml;

    }

    /**
     * エビデンスファイルに出力する文字列を構築します。
     *
     * @return エビデンスファイルに出力する文字列
     */
    private String build(Evidence evidence) {
        VelocityContext context = new VelocityContext();
        StringWriter writer = new StringWriter();
        context.put("records", evidence.getRecords());
        context.put("caseNo", evidence.getCaseNo());
        context.put("testScriptName", evidence.getScriptName());
        context.put("result", (evidence.hasError()) ? "NG" : "");

        Future<?> screenshotRezeFuture = evidence.getScreenshotResizeFuture();

        if (screenshotRezeFuture != null) {
            try {
                screenshotRezeFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOG.warn("スクリーンショットリサイズ処理の待機で例外発生", e);
            }
        }

        tmpl.merge(context, writer);
        return writer.toString();
    }

    @PreDestroy
    public void moveLogFile() {
        try {
            File logFile = new File(logFilePath);
            FileUtils.copyFileToDirectory(logFile, evidenceDir, true);
            logFile.deleteOnExit();
        } catch (IOException e) {
            throw new TestException(e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        appCtx = arg0;
    }

}
