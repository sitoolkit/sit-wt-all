package org.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yu.takada
 *
 */
public class MaskEvidenceGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MaskEvidenceGenerator.class);

    private static String MASK_PREFIX = "mask_";

    public void generate(EvidenceDir targetDir) {

        LOG.info("マスク済みエビデンスを生成します");

        if (!(targetDir.exists())) {
            LOG.info("エビデンスがありません");
            return;
        }

        for (File evidenceFile : targetDir.getEvidenceFiles()) {

            Map<String, File> ssMap = targetDir.getScreenshotFilesAsMap(evidenceFile.getName());

            for (Entry<String, File> screenshot : ssMap.entrySet()) {
                if (screenshot.getKey().startsWith(MASK_PREFIX)) {
                    generateMaskEvidence(targetDir, evidenceFile, ssMap);
                    break;
                }
            }

        }

    }

    void generateMaskEvidence(EvidenceDir evidenceDir, File evidenceFile, Map<String, File> ssMap) {

        File maskEvidence = new File(evidenceDir.getDir(), MASK_PREFIX + evidenceFile.getName());

        try {
            String evidenceHtml = FileUtils.readFileToString(evidenceFile, "UTF-8");

            for (Entry<String, File> ssName : ssMap.entrySet()) {
                if (ssName.getKey().startsWith(MASK_PREFIX)) {
                    String target = StringUtils.removeStart(ssName.getKey(), MASK_PREFIX);
                    evidenceHtml = StringUtils.replace(evidenceHtml, target, ssName.getKey());
                }
            }

            FileUtils.writeStringToFile(maskEvidence, evidenceHtml, "UTF-8");
            LOG.info("マスク済みエビデンスを生成しました {}", maskEvidence.getPath());

        } catch (IOException e) {
            LOG.error("エビデンス生成処理で例外が発生しました", e);
        }

    }

}
