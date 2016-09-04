package org.sitoolkit.wt.domain.evidence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.infra.template.TemplateEngine;
import org.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * 比較エビデンスを生成するGeneratorです。 比較エビデンスとはスクリーンショットを目視で比較するためのエビデンスで、
 * 2つのエビデンス(html)を左右に並べて見えるようにしたhtmlファイルです。
 *
 *
 * @author yu.takada
 *
 */
public class DiffEvidenceGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(DiffEvidenceGenerator.class);

    private static final String COMPARE_PREFIX = "comp_";

    private static final String COMPARE_NG_PREFIX = "comp_ng_";

    private static final String UNMATCH_PREFIX = "unmatch_";

    private static final String failsafeReportName = "failsafe-report.html";

    private String evidenceFileRegex = ".*\\.html$";

    /**
     * エビデンスの表示に関連する資源
     */
    private String compareEvidenceResource = "js/diff.js";

    private DiffEvidence compareEvidence;

    private TemplateEngine templateEngine;

    public static void main(String[] args) {
        DiffEvidenceGenerator generator = new DiffEvidenceGenerator();
        generator.setCompareEvidence(new DiffEvidence());
        // TODO
        TemplateEngineVelocityImpl engine = new TemplateEngineVelocityImpl();
        engine.init();
        generator.setTemplateEngine(engine);
        generator.run("default", false);

    }

    /**
     * 基準エビデンスと比較対象エビデンスとを比較し、差分エビデンスを生成します。
     * 差分エビデンスは、対象エビデンスディレクトリ内のエビデンスファイル(html)と、同じファイル名の基準エビデンスに対し生成します。
     * 差分エビデンスのファイルは対象エビデンスディレクトリ内に生成します。
     *
     * @param targetEvidenceDir
     *            対象エビデンスディレクトリ
     * @param browser
     *            対象エビデンスのテスト実行に使用したブラウザ
     * @see EvidenceUtils#baseEvidenceDir(String)
     */
    public void generate(String targetEvidenceDir, String browser) {
        File baseDir = EvidenceUtils.baseEvidenceDir(browser);
        File targetDir = new File(targetEvidenceDir);

        for (File htmlFile : FileUtils.listFiles(targetDir, new String[] { "html" }, true)) {

            String htmlName = htmlFile.getName();
            if (htmlName.startsWith(COMPARE_PREFIX) || htmlName.equals(failsafeReportName)) {
                continue;
            }

            generateDiffEvidence(baseDir, targetDir, htmlName);
        }

    }

    void generateDiffEvidence(File baseDir, File targetDir, String htmlName) {
        // TODO 実装
    }

    public void run(String mainBrowser, boolean isUnmatchCompare) {
        File latestEvidenceDir = EvidenceUtils.getLatestEvidenceDir();

        if (latestEvidenceDir == null) {
            return;
        }

        String driverType = System.getProperty("driver.type");

        String baseBrowser;
        if (isUnmatchCompare) { // or isScreenshotCompare
            baseBrowser = driverType;
        } else {
            baseBrowser = mainBrowser;
            if (mainBrowser.equals(driverType)) {
                return;
            }
        }

        File baseEvidenceDir = EvidenceUtils.baseEvidenceDir(baseBrowser);

        LOG.info("{} <-> {}", baseEvidenceDir, latestEvidenceDir);

        for (File s : FileUtils.listFiles(latestEvidenceDir, new RegexFileFilter(evidenceFileRegex),
                TrueFileFilter.INSTANCE)) {
            String htmlName = s.getName();
            if (!(htmlName.startsWith(COMPARE_PREFIX)) && !(failsafeReportName.equals(htmlName))) {
                if (isUnmatchCompare
                        && matchedEvidence(htmlName, compareEvidence.getUnmatchScreenshotNames())) {
                    // 不一致スクリーンショットが存在しないエビデンスでは、比較エビデンスを作成しない
                    continue;
                }
                createEvidence(baseEvidenceDir, latestEvidenceDir.getPath(), htmlName,
                        isUnmatchCompare, compareEvidence.getUnmatchScreenshotNames());
            }
        }

    }

    private boolean matchedEvidence(String htmlName, List<String> errorScreenshotNames) {

        for (String s : errorScreenshotNames) {
            if (s.startsWith(htmlName.replaceFirst(".html$", ""))) {
                return false;
            }
        }
        return true;
    }

    private void createEvidence(File baseEvidencePath, String targetEvidencePath,
            String evidenceName, boolean isUnmatchCompare, List<String> errorScreenshotNames) {

        // 基準エビデンスが存在しなければ何もしない
        File baseEvidence = new File(baseEvidencePath, evidenceName);
        if (!(baseEvidence.exists())) {
            LOG.info("基準となるエビデンスが存在しませんでした {}", baseEvidence.getName());
            return;
        }

        File targetEvidence = new File(EvidenceUtils.concatPath(targetEvidencePath, evidenceName));
        File targetEvidenceDir = new File(targetEvidencePath);

        try {

            String baseEvidenceHtml = EvidenceUtils.extractTable(baseEvidence);
            String targetEvidenceHtml = EvidenceUtils.extractTable(targetEvidence);

            String baseEvidenceImgPath;
            if (isUnmatchCompare) {
                baseEvidenceImgPath = "img/base_ng";
                compareEvidence.setEvidenceName(COMPARE_NG_PREFIX.concat(baseEvidence.getName()));
            } else {
                baseEvidenceImgPath = "img/base";
                compareEvidence.setEvidenceName(COMPARE_PREFIX.concat(baseEvidence.getName()));
            }

            compareEvidence.setLeftFileName(
                    EvidenceUtils.concatPath(baseEvidence.getParent(), baseEvidence.getName()));
            compareEvidence.setRightFileName(
                    EvidenceUtils.concatPath(targetEvidence.getParent(), targetEvidence.getName()));

            compareEvidence.setLeftFile(StringUtils.replace(baseEvidenceHtml, "src=\"img",
                    "src=\"".concat(baseEvidenceImgPath)));

            if (isUnmatchCompare) {
                for (String s : errorScreenshotNames) {
                    if (s.startsWith(evidenceName.replaceFirst(".html$", ""))) {
                        targetEvidenceHtml = targetEvidenceHtml.replaceAll(s,
                                UNMATCH_PREFIX.concat(s));
                    }
                }
            }

            compareEvidence.setRightFile(targetEvidenceHtml);

            String evidenceStr = templateEngine.writeToString(compareEvidence);

            File evidence = new File(targetEvidenceDir, compareEvidence.getEvidenceName());
            FileUtils.write(evidence, evidenceStr, "UTF-8");

            // diff.jsのコピー
            URL url;
            url = ResourceUtils.getURL("classpath:evidence/" + compareEvidenceResource);
            File dstFile = new File(targetEvidenceDir, compareEvidenceResource);
            FileUtils.copyURLToFile(url, dstFile);

            String imgPathLeftTo = EvidenceUtils.concatPath(targetEvidencePath,
                    baseEvidenceImgPath);

            File imgLeftDirFrom = new File(baseEvidence.getParent().concat("/img"));
            File imgLeftDirTo = new File(imgPathLeftTo);
            copy(imgLeftDirFrom, imgLeftDirTo);

            LOG.info("差分エビデンスを生成しました {}", evidence);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void copy(File srcDir, File destDir) {

        for (File f : srcDir.listFiles()) {
            try {
                FileUtils.copyFileToDirectory(f, destDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public DiffEvidence getCompareEvidence() {
        return compareEvidence;
    }

    public void setCompareEvidence(DiffEvidence compareEvidence) {
        this.compareEvidence = compareEvidence;
    }

}
