package org.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.DiffEvidence;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.domain.evidence.EvidenceUtils;
import org.sitoolkit.wt.infra.template.TemplateEngine;
import org.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
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

    @Resource
    ApplicationContext appCtx;

    private static final Logger LOG = LoggerFactory.getLogger(DiffEvidenceGenerator.class);

    private static final String COMPARE_PREFIX = "comp_";

    private static final String COMPARE_NG_PREFIX = "comp_ng_";

    private static final String UNMATCH_PREFIX = "unmatch_";

    private static final String failsafeReportName = "failsafe-report.html";

    private static final String IMG_BASE = "base";

    private static final String IMG_BASE_DIFF = "base_diff";

    private String evidenceFileRegex = ".*\\.html$";

    /**
     * エビデンスの表示に関連する資源
     */
    private String compareEvidenceResource = "js/diff.js";

    private DiffEvidence compareEvidence;

    private TemplateEngine templateEngine;

    private ScreenshotComparator screenshotComparator = new ScreenshotComparator();

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
     * 基準エビデンスと対象エビデンスとの比較エビデンスを生成します。
     * 比較エビデンスは、基準と対象それぞれのエビデンスディレクトリ内の同じファイル名のエビデンスファイル(html)に対して生成します。
     * 比較エビデンスのファイルは対象エビデンスディレクトリ内に生成します。
     *  {@code compareScreenshot}に{@code true}を指定すると、 
     *  エビデンス同士のスクリーンショットを比較し、それに対する比較エビデンスも生成します。
     *
     * @param baseDir
     *            基準エビデンスディレクトリ
     *
     * @param targetDir
     *            対象エビデンスディレクトリ
     *
     * @param compareScreenshot
     *            基準と対象のエビデンスのスクリーンショットの比較を行う場合にtrue
     * @return 比較対象エビデンスの全スクリーンショットが基準と一致する場合にtrue (スクリーンショットの比較を行わない場合は常にtrue)
     */
    public boolean generate(EvidenceDir baseDir, EvidenceDir targetDir, boolean compareScreenshot) {

        LOG.info("比較エビデンスを生成します {} <-> {}", baseDir.getDir(), targetDir.getDir());

        boolean allSsMatches = true;

        for (File evidenceFile : targetDir.getEvidenceFiles()) {

            generateDiffEvidence(baseDir, evidenceFile, false);

            if (compareScreenshot) {

                MaskScreenshotGenerator mask = appCtx.getBean(MaskScreenshotGenerator.class);
                mask.generate(targetDir);

                if (!screenshotComparator.compare(baseDir, targetDir, evidenceFile)) {
                    generateDiffEvidence(baseDir, evidenceFile, true);
                    allSsMatches = false;
                }
            }

        }

        return allSsMatches;

    }

    /**
     * 比較エビデンスを生成します。
     *
     * @param baseEvidenceDir
     *            基準エビデンスディレクトリ
     * @param evidenceFile
     *            比較エビデンスの生成対象のエビデンス
     * @param withUnmatch
     *            不一致スクリーンショットに対する比較エビデンスを生成する場合にtrue
     */
    void generateDiffEvidence(EvidenceDir baseEvidenceDir, File evidenceFile, boolean withUnmatch) {

        File baseEvidenceFile = null;
        for (File f : baseEvidenceDir.getEvidenceFiles()) {
            if (evidenceFile.getName().equals(f.getName())) {
                baseEvidenceFile = f;
                break;
            }
        }
        if (baseEvidenceFile == null) {
            LOG.info("基準エビデンスが存在しないため、比較エビデンス作成処理を終了します {}", evidenceFile.getName());
            return;
        }

        DiffEvidence diffEvidence = appCtx.getBean(DiffEvidence.class);
        load(diffEvidence, baseEvidenceFile, evidenceFile, withUnmatch);

        templateEngine.write(diffEvidence);

        copyBaseScreenshots(baseEvidenceDir, baseEvidenceFile, evidenceFile, withUnmatch);

        try {
            URL url = ResourceUtils.getURL("classpath:evidence/" + compareEvidenceResource);
            File dstFile = new File(evidenceFile.getParent(), compareEvidenceResource);
            FileUtils.copyURLToFile(url, dstFile);
        } catch (IOException e) {
            LOG.error("リソースファイルのコピー処理で例外が発生しました", e);
        }

    }

    private void copyBaseScreenshots(EvidenceDir baseEvidenceDir, File baseEvidenceFile,
            File evidenceFile, boolean withUnmatch) {

        LOG.info("基準のスクリーンショットをエビデンスディレクトリにコピーします");

        try {

            String baseImgFolder = withUnmatch ? IMG_BASE_DIFF : IMG_BASE;
            File dstDir = new File(StringUtils
                    .join(new String[] { evidenceFile.getParent(), "img", baseImgFolder }, "/"));

            Map<String, File> baseSsMap = baseEvidenceDir
                    .getScreenshotFilesAsMap(baseEvidenceFile.getName());

            for (Entry<String, File> imgFile : baseSsMap.entrySet()) {
                LOG.info("基準のスクリーンショットをコピーします {}, {} -> {}", imgFile.getValue().getName(),
                        imgFile.getValue().getParent(), dstDir.getPath());
                FileUtils.copyFileToDirectory(imgFile.getValue(), dstDir);
            }

        } catch (IOException e) {
            LOG.error("スクリーンショットのコピー処理で例外が発生しました", e);
        }

    }

    void load(DiffEvidence diffEvidence, File baseEvidenceFile, File evidenceFile,
            boolean withUnmatch) {

        diffEvidence.setEvidenceName(StringUtils.removeEnd(evidenceFile.getName(), ".html"));

        String prefix = withUnmatch ? COMPARE_NG_PREFIX : COMPARE_PREFIX;
        diffEvidence.setFileBase(StringUtils.join(prefix, diffEvidence.getEvidenceName()));

        diffEvidence.setOutDir(evidenceFile.getParent());

        diffEvidence.setLeftFileName(StringUtils.join(
                new String[] { baseEvidenceFile.getParent(), baseEvidenceFile.getName() }, "/"));
        diffEvidence.setRightFileName(StringUtils
                .join(new String[] { evidenceFile.getParent(), evidenceFile.getName() }, "/"));

        try {
            String baseImgFolder = withUnmatch ? IMG_BASE_DIFF : IMG_BASE;

            String leftHtmlTable = EvidenceUtils.extractTable(baseEvidenceFile);
            diffEvidence.setLeftFile(StringUtils.replace(leftHtmlTable, "src=\"img",
                    "src=\"img/".concat(baseImgFolder)));

            String rightHtmlTable = EvidenceUtils.extractTable(evidenceFile);

            // TODO rightHtmlTableのスクリーンショットリンクにUNMATCH_PREFIXをつける
            if (withUnmatch) {

            }

            diffEvidence.setRightFile(rightHtmlTable);

        } catch (IOException e) {
            LOG.info("比較エビデンス生成処理で例外が発生しました", e);
        }

    }

    @Deprecated
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

            LOG.info("比較エビデンスを生成しました {}", evidence);

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
