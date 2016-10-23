package org.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.DiffEvidence;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.domain.evidence.EvidenceUtils;
import org.sitoolkit.wt.domain.evidence.ReportOpener;
import org.sitoolkit.wt.infra.TestException;
import org.sitoolkit.wt.infra.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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

    private static final String MASK_PREFIX = "mask_";

    private static final String UNMATCH_PREFIX = "unmatch_";

    private static final String IMG_BASE = "base";

    /**
     * エビデンスの表示に関連する資源
     */
    private String compareEvidenceResource = "js/diff.js";

    private DiffEvidence compareEvidence;

    private TemplateEngine templateEngine;

    private ScreenshotComparator screenshotComparator = new ScreenshotComparator();

    public static void staticExecute(EvidenceDir baseDir, EvidenceDir targetDir,
            boolean compareScreenshot, String evidenceOpen) {

        if (!(baseDir.exists())) {
            LOG.info("基準エビデンスがありません");
            return;
        } else {

            ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                    DiffEvidenceGeneratorConfig.class);
            DiffEvidenceGenerator generator = appCtx.getBean(DiffEvidenceGenerator.class);

            boolean result = generator.generate(baseDir, targetDir, compareScreenshot);

            if (!result) {
                EvidenceReportEditor editor = appCtx.getBean(EvidenceReportEditor.class);
                editor.edit();

                if (Boolean.parseBoolean(evidenceOpen)) {
                    ReportOpener opener = appCtx.getBean(ReportOpener.class);
                    opener.open();
                }
                throw new TestException("基準と異なるスクリーンショットが存在します");
            }
        }
    }

    /**
     * 基準エビデンスと対象エビデンスとの比較エビデンスを生成します。
     * 比較エビデンスは、基準と対象それぞれのエビデンスディレクトリ内の同じファイル名のエビデンスファイル(html)に対して生成します。
     * 比較エビデンスのファイルは対象エビデンスディレクトリ内に生成します。
     * {@code compareScreenshot}に{@code true}を指定すると、
     * エビデンス同士のスクリーンショットを比較し、それに対する比較エビデンスも生成します。
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

        if (compareScreenshot) {
            MaskScreenshotGenerator mask = appCtx.getBean(MaskScreenshotGenerator.class);
            MaskEvidenceGenerator evidence = new MaskEvidenceGenerator();
            mask.generate(targetDir);
            evidence.generate(targetDir);
        }

        boolean allSsMatches = true;

        for (File evidenceFile : targetDir.getEvidenceFiles()) {

            if (compareScreenshot) {
                if (!screenshotComparator.compare(baseDir, targetDir, evidenceFile)) {
                    generateDiffEvidence(baseDir, evidenceFile, true);
                    allSsMatches = false;
                }
            }

            generateDiffEvidence(baseDir, evidenceFile, false);
            copyBaseScreenshots(baseDir, evidenceFile);

        }

        try {
            URL url = ResourceUtils.getURL("classpath:evidence/" + compareEvidenceResource);
            File dstFile = new File(targetDir.getDir(), compareEvidenceResource);
            FileUtils.copyURLToFile(url, dstFile);
        } catch (IOException e) {
            LOG.error("リソースファイルのコピー処理で例外が発生しました", e);
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

        File baseEvidence = new File(baseEvidenceDir.getDir(), evidenceFile.getName());

        DiffEvidence diffEvidence = appCtx.getBean(DiffEvidence.class);
        load(diffEvidence, baseEvidenceDir, baseEvidence, evidenceFile, withUnmatch);

        templateEngine.write(diffEvidence);

        File maskEvidence = new File(evidenceFile.getParent(),
                MASK_PREFIX + evidenceFile.getName());
        if (!withUnmatch && maskEvidence.exists()) {
            generateMaskDiffEvidence(diffEvidence, baseEvidence, evidenceFile, withUnmatch);
        }

    }

    private void generateMaskDiffEvidence(DiffEvidence diffEvidence, File baseEvidenceFile,
            File evidenceFile, boolean withUnmatch) {

        load(diffEvidence, baseEvidenceFile, evidenceFile, withUnmatch);
        templateEngine.write(diffEvidence);

    }

    void load(DiffEvidence diffEvidence, EvidenceDir baseEvidenceDir, File baseEvidenceFile,
            File evidenceFile, boolean withUnmatch) {

        diffEvidence.setEvidenceName(StringUtils.removeEnd(evidenceFile.getName(), ".html"));

        String prefix = withUnmatch ? COMPARE_NG_PREFIX : COMPARE_PREFIX;
        diffEvidence.setFileBase(prefix + diffEvidence.getEvidenceName());

        diffEvidence.setOutDir(evidenceFile.getParent());

        diffEvidence.setLeftFileName(FilenameUtils.concat(baseEvidenceDir.getDir().getPath(),
                baseEvidenceFile.getName()));
        diffEvidence.setRightFileName(
                FilenameUtils.concat(evidenceFile.getParent(), evidenceFile.getName()));

        try {
            String leftHtmlTable = EvidenceUtils.extractTable(baseEvidenceFile);
            leftHtmlTable = EvidenceUtils.removeInputLine(leftHtmlTable);
            leftHtmlTable = StringUtils.replace(leftHtmlTable, "src=\"img",
                    "src=\"img/" + IMG_BASE);

            String rightHtmlTable = EvidenceUtils.extractTable(evidenceFile);
            rightHtmlTable = EvidenceUtils.removeInputLine(rightHtmlTable);

            if (withUnmatch) {
                leftHtmlTable = replaceImgName(leftHtmlTable, baseEvidenceFile);
                rightHtmlTable = replaceImgName(rightHtmlTable, evidenceFile);
            }

            diffEvidence.setLeftFile(leftHtmlTable);
            diffEvidence.setRightFile(rightHtmlTable);

        } catch (IOException e) {
            LOG.error("比較エビデンス生成処理で例外が発生しました", e);
        }

    }

    void load(DiffEvidence diffEvidence, File baseEvidenceFile, File evidenceFile,
            boolean withUnmatch) {

        diffEvidence.setFileBase(
                StringUtils.join(COMPARE_PREFIX, MASK_PREFIX, diffEvidence.getEvidenceName()));
        diffEvidence.setLeftFile(
                replaceMaskImgName(diffEvidence.getLeftFile(), baseEvidenceFile, withUnmatch));
        diffEvidence.setRightFile(
                replaceMaskImgName(diffEvidence.getRightFile(), evidenceFile, withUnmatch));

    }

    private String replaceImgName(String text, File evidenceFile) {

        EvidenceDir evidenceDir = EvidenceDir.getInstance(evidenceFile.getParent());
        Map<String, File> imgMap = evidenceDir.getScreenshotFilesAsMap(evidenceFile.getName());

        for (Entry<String, File> imgFile : imgMap.entrySet()) {

            String imgName = imgFile.getKey();

            if (imgName.startsWith(MASK_PREFIX) || imgName.startsWith(UNMATCH_PREFIX)) {
                continue;
            }

            String unmatchMaskImgName = UNMATCH_PREFIX + MASK_PREFIX + imgName;
            String unmatchImgName = UNMATCH_PREFIX + imgName;
            String maskImgName = MASK_PREFIX + imgName;

            if (imgMap.get(unmatchMaskImgName) != null) {
                text = StringUtils.replace(text, imgName, unmatchMaskImgName);
            } else if (imgMap.get(unmatchImgName) != null) {
                text = StringUtils.replace(text, imgName, unmatchImgName);
            } else if (imgMap.get(maskImgName) != null) {
                text = StringUtils.replace(text, imgName, maskImgName);
            }

        }

        return text;
    }

    private String replaceMaskImgName(String text, File evidenceFile, boolean withUnmatch) {

        EvidenceDir evidenceDir = EvidenceDir.getInstance(evidenceFile.getParent());
        Map<String, File> imgMap = evidenceDir.getScreenshotFilesAsMap(evidenceFile.getName());

        for (Entry<String, File> imgFile : imgMap.entrySet()) {

            String imgName = imgFile.getKey();

            if (imgName.startsWith(MASK_PREFIX)) {
                continue;
            }

            String maskImgName = MASK_PREFIX + imgName;
            if (imgMap.get(maskImgName) != null) {
                text = StringUtils.replace(text, imgName, maskImgName);
            }

        }

        return text;
    }

    private void copyBaseScreenshots(EvidenceDir baseDir, File evidenceFile) {

        LOG.info("基準のスクリーンショットをエビデンスディレクトリにコピーします");

        try {

            File dstDir = new File(StringUtils
                    .join(new String[] { evidenceFile.getParent(), "img", IMG_BASE }, "/"));

            Map<String, File> baseSsMap = baseDir.getScreenshotFilesAsMap(evidenceFile.getName());

            for (Entry<String, File> imgFile : baseSsMap.entrySet()) {
                FileUtils.copyFileToDirectory(imgFile.getValue(), dstDir);
            }

        } catch (IOException e) {
            LOG.error("スクリーンショットのコピー処理で例外が発生しました", e);
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
