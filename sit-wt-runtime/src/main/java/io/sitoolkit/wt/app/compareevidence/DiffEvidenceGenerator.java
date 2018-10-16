package io.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.domain.evidence.DiffEvidence;
import io.sitoolkit.wt.domain.evidence.EvidenceDir;
import io.sitoolkit.wt.domain.evidence.EvidenceOpener;
import io.sitoolkit.wt.domain.evidence.ReportOpener;
import io.sitoolkit.wt.infra.TestException;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.template.TemplateEngine;

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

    private static final SitLogger LOG = SitLoggerFactory.getLogger(DiffEvidenceGenerator.class);

    /**
     * エビデンスの表示に関連する資源
     */
    private String compareEvidenceResource = "js/diff.js";

    private DiffEvidence compareEvidence;

    private TemplateEngine templateEngine;

    private ScreenshotComparator screenshotComparator = new ScreenshotComparator();

    public static void main(String[] args) {
        DiffEvidenceGenerator.staticExecute(args);
    }

    public static void staticExecute(String[] args) {
        ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                DiffEvidenceGeneratorConfig.class);
        DiffEvidenceGenerator generator = appCtx.getBean(DiffEvidenceGenerator.class);

        String targetPath = null;
        String basePath = null;
        int argCount = args.length;

        if (argCount == 2) {
            basePath = args[0];
            targetPath = args[1];
        } else if (argCount == 1) {
            targetPath = args[0];
        }

        EvidenceDir targetDir = EvidenceDir.targetEvidenceDir(targetPath);
        EvidenceDir baseDir = EvidenceDir.baseEvidenceDir(basePath, targetDir.getBrowser());

        generator.generate(baseDir, targetDir, false);

        EvidenceOpener opener = new EvidenceOpener();
        opener.openCompareEvidence(targetDir);

    }

    public static void staticExecute(EvidenceDir baseDir, EvidenceDir targetDir,
            boolean compareScreenshot, boolean evidenceOpen) {

        if (!(baseDir.exists())) {
            LOG.error("base.evidence.error");
        } else {

            ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                    DiffEvidenceGeneratorConfig.class);
            DiffEvidenceGenerator generator = appCtx.getBean(DiffEvidenceGenerator.class);

            boolean result = generator.generate(baseDir, targetDir, compareScreenshot);

            if (!result) {
                EvidenceReportEditor editor = appCtx.getBean(EvidenceReportEditor.class);
                editor.edit(targetDir);

                if (evidenceOpen) {
                    ReportOpener opener = appCtx.getBean(ReportOpener.class);
                    opener.open(targetDir);
                }
                throw new TestException("基準と異なるスクリーンショットが存在します");
            }
        }
    }

    /**
     * 基準エビデンスと対象エビデンスとの比較エビデンスを生成します。
     * 比較エビデンスは、基準と対象それぞれのエビデンスディレクトリ内の同じファイル名のエビデンスファイル(html)に対して生成します。
     * 比較エビデンスのファイルは対象エビデンスディレクトリ内に生成します。 {@code compareScreenshot}に{@code true}
     * を指定すると、 エビデンス同士のスクリーンショットを比較し、それに対する比較エビデンスも生成します。
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

        LOG.info("compare.screenshot", baseDir.getDir(), targetDir.getDir());

        if (compareScreenshot) {
            MaskScreenshotGenerator mask = appCtx.getBean(MaskScreenshotGenerator.class);
            mask.generate(targetDir);
            MaskEvidenceGenerator evidence = appCtx.getBean(MaskEvidenceGenerator.class);
            evidence.generate(targetDir);
        }

        boolean allSsMatches = true;

        for (File evidenceFile : targetDir.getEvidenceFiles()) {

            if (compareScreenshot) {
                if (!screenshotComparator.compare(baseDir, targetDir, evidenceFile)) {
                    allSsMatches = false;
                    try {
                        generateDiffEvidence(baseDir, evidenceFile, true);
                    } catch (IOException e) {
                        LOG.error("compare.screenshot.error", e);
                        return allSsMatches;
                    }
                }
            }

            try {
                generateDiffEvidence(baseDir, evidenceFile, false);
                copyBaseScreenshots(baseDir, targetDir, evidenceFile);
            } catch (IOException e) {
                LOG.error("compare.screenshot.error", e);
                return allSsMatches;
            }

        }

        try {
            URL url = ResourceUtils.getURL("classpath:evidence/" + compareEvidenceResource);
            File dstFile = new File(targetDir.getDir(), compareEvidenceResource);
            FileUtils.copyURLToFile(url, dstFile);
        } catch (IOException e) {
            LOG.error("resource.copy.error", e);
        } catch (Exception exp) {
            LOG.error("proxy.error", exp);
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
     * @throws IOException
     *             入出力エラーが発生した場合または基準エビデンスが存在しない場合
     */
    void generateDiffEvidence(EvidenceDir baseEvidenceDir, File evidenceFile, boolean withUnmatch)
            throws IOException {

        File baseEvidence = new File(baseEvidenceDir.getDir(), evidenceFile.getName());

        DiffEvidence diffEvidence = appCtx.getBean(DiffEvidence.class);
        load(diffEvidence, baseEvidenceDir, baseEvidence, evidenceFile, withUnmatch);

        templateEngine.write(diffEvidence);

        File maskEvidence = new File(evidenceFile.getParent(),
                EvidenceDir.toMaskEvidenceName(evidenceFile.getName()));

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
            File evidenceFile, boolean withUnmatch) throws IOException {

        diffEvidence.setEvidenceName(StringUtils.removeEnd(evidenceFile.getName(), ".html"));

        diffEvidence.setFileBase(
                EvidenceDir.getCompareEvidencePrefix(withUnmatch) + diffEvidence.getEvidenceName());

        diffEvidence.setOutDir(evidenceFile.getParent());

        diffEvidence.setLeftFileName(FilenameUtils.concat(baseEvidenceDir.getDir().getPath(),
                baseEvidenceFile.getName()));
        diffEvidence.setRightFileName(
                FilenameUtils.concat(evidenceFile.getParent(), evidenceFile.getName()));

        String leftHtmlTable = EvidenceDir.extractTable(baseEvidenceFile);
        leftHtmlTable = EvidenceDir.removeInputLine(leftHtmlTable);
        leftHtmlTable = EvidenceDir.replaceImgPath(leftHtmlTable);

        String rightHtmlTable = EvidenceDir.extractTable(evidenceFile);
        rightHtmlTable = EvidenceDir.removeInputLine(rightHtmlTable);

        if (withUnmatch) {
            leftHtmlTable = replaceImgName(leftHtmlTable, baseEvidenceFile);
            rightHtmlTable = replaceImgName(rightHtmlTable, evidenceFile);
        }

        diffEvidence.setLeftFile(leftHtmlTable);
        diffEvidence.setRightFile(rightHtmlTable);

    }

    void load(DiffEvidence diffEvidence, File baseEvidenceFile, File evidenceFile,
            boolean withUnmatch) {

        diffEvidence
                .setFileBase(EvidenceDir.toCompareMaskEvidenceName(diffEvidence.getEvidenceName()));
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

            if (EvidenceDir.isMaskScreenshot(imgName)
                    || EvidenceDir.isUnmatchMaskScreenshot(imgName)) {
                continue;
            }

            String unmatchMaskImgName = EvidenceDir.toUnmatchMaskSsName(imgName);
            String unmatchImgName = EvidenceDir.toUnmatchSsName(imgName);
            String maskImgName = EvidenceDir.toMaskSsName(imgName);

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

            if (EvidenceDir.isMaskScreenshot(imgName)) {
                continue;
            }

            String maskImgName = EvidenceDir.toMaskSsName(imgName);
            if (imgMap.get(maskImgName) != null) {
                text = StringUtils.replace(text, imgName, maskImgName);
            }

        }

        return text;
    }

    private void copyBaseScreenshots(EvidenceDir baseDir, EvidenceDir targetDir,
            File evidenceFile) {

        LOG.info("copy.base.screenshots");

        try {

            Map<String, File> baseSsMap = baseDir.getScreenshotFilesAsMap(evidenceFile.getName());

            for (Entry<String, File> imgFile : baseSsMap.entrySet()) {
                FileUtils.copyFileToDirectory(imgFile.getValue(), targetDir.getImgBaseDir());
            }

        } catch (IOException e) {
            LOG.error("copy.screenshot.error", e);
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
