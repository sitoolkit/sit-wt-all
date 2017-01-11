package org.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.domain.evidence.EvidenceOpener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yu.takada
 *
 */
public class MaskEvidenceGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MaskEvidenceGenerator.class);

    public static void main(String[] args) {

        EvidenceDir targetDir = EvidenceDir.targetEvidenceDir(args[0]);

        MaskScreenshotGenerator mask = new MaskScreenshotGenerator();
        mask.generate(targetDir);

        MaskEvidenceGenerator evidence = new MaskEvidenceGenerator();
        evidence.generate(targetDir);

        EvidenceOpener opener = new EvidenceOpener();
        opener.openMaskEvidence(targetDir);

    }

    public void generate(EvidenceDir targetDir) {

        LOG.info("マスク済みエビデンスを生成します");

        if (!(targetDir.exists())) {
            LOG.error("エビデンスがありません");
            return;
        }

        for (File evidenceFile : targetDir.getEvidenceFiles()) {

            Map<String, File> ssMap = targetDir.getScreenshotFilesAsMap(evidenceFile.getName());

            for (Entry<String, File> screenshot : ssMap.entrySet()) {
                if (EvidenceDir.isMaskScreenshot(screenshot.getKey())) {
                    generateMaskEvidence(evidenceFile, ssMap);
                    break;
                }
            }

        }

    }

    void generateMaskEvidence(File evidenceFile, Map<String, File> ssMap) {

        File maskEvidence = new File(evidenceFile.getParent(),
                EvidenceDir.toMaskEvidenceName(evidenceFile.getName()));

        try {
            String evidenceHtml = FileUtils.readFileToString(evidenceFile, "UTF-8");

            for (Entry<String, File> ssName : ssMap.entrySet()) {
                if (EvidenceDir.isMaskScreenshot(ssName.getKey())) {
                    evidenceHtml = StringUtils.replace(evidenceHtml,
                            EvidenceDir.toBeforeMaskSsName(ssName.getKey()), ssName.getKey());
                }
            }

            FileUtils.writeStringToFile(maskEvidence, evidenceHtml, "UTF-8");
            LOG.info("マスク済みエビデンスを生成しました {}", maskEvidence.getPath());

        } catch (IOException e) {
            LOG.error("エビデンス生成処理で例外が発生しました", e);
        }

    }

}
