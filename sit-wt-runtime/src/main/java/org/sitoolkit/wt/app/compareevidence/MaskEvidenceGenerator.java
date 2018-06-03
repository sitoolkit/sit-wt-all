package org.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.domain.evidence.EvidenceOpener;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;

/**
 * @author yu.takada
 *
 */
public class MaskEvidenceGenerator {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(MaskEvidenceGenerator.class);

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

        LOG.info("mask.evidence.generate");

        if (!(targetDir.exists())) {
            LOG.error("evidence.error");
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
            LOG.info("mask.evidence.generated", maskEvidence.getPath());

        } catch (IOException e) {
            LOG.error("evidence.generate.error", e);
        }

    }

}
