package org.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;

public class BaseEvidenceManager {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(BaseEvidenceManager.class);

    public static void main(String[] args) {
        EvidenceDir targetDir = EvidenceDir.targetEvidenceDir(args[0]);
        BaseEvidenceManager baseEvidenceManager = new BaseEvidenceManager();
        baseEvidenceManager.setBaseEvidence(targetDir);
    }

    /**
     * 指定されたエビデンスを基準エビデンスとして確定します。
     *
     * @param targetDir
     *            基準として確定するエビデンスディレクトリ
     */
    public void setBaseEvidence(EvidenceDir targetDir) {

        if (!(targetDir.exists())) {
            LOG.error("evidence.error");
        } else {
            EvidenceDir baseDir = EvidenceDir.getBase(targetDir.getBrowser());
            LOG.info("base.evidence.set", targetDir.getDir());
            copy(targetDir.getDir(), baseDir.getDir());

        }

    }

    public void copy(File srcDir, File destDir) {

        for (File f1 : srcDir.listFiles()) {
            try {

                if (!isCopyTarget(f1)) {
                    continue;
                }

                if (f1.isDirectory()) {
                    copy(f1, new File(destDir.getPath(), f1.getName()));
                } else {
                    FileUtils.copyFileToDirectory(f1, destDir);
                }

            } catch (IOException e) {
                LOG.error("base.evidence.copy", e);
            }
        }

    }

    private boolean isCopyTarget(File f1) {
        boolean result = true;
        result &= !EvidenceDir.isBaseImgDir(f1);
        result &= !EvidenceDir.isProjectReport(f1.getName());
        result &= !EvidenceDir.isFailsafeReport(f1.getName());
        result &= !EvidenceDir.isCompareEvidence(f1.getName());
        result &= !EvidenceDir.isCompareNgEvidence(f1.getName());
        result &= !EvidenceDir.isUnmatchScreenshot(f1.getName());
        result &= !EvidenceDir.isUnmatchMaskScreenshot(f1.getName());
        result &= !StringUtils.equals(f1.getName(), "sit-wt.log");
        result &= !StringUtils.equals(f1.getName(), "sit-wt.properties");
        return result;
    }

}
