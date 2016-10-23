package org.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseEvidenceManager {

    private static final Logger LOG = LoggerFactory.getLogger(BaseEvidenceManager.class);

    private static final String COMPARE_PREFIX = "comp_";

    public static void main(String[] args) {
        BaseEvidenceManager manager = new BaseEvidenceManager();
        manager.setBaseEvidence(EvidenceDir.getLatest());
    }

    /**
     * 指定されたエビデンスを基準エビデンスとして確定します。
     *
     * @param targetDir
     *            基準として確定するエビデンスディレクトリ
     */
    public void setBaseEvidence(EvidenceDir targetDir) {

        if (!(targetDir.exists())) {
            LOG.info("エビデンスがありません");
        } else {
            EvidenceDir baseDir = EvidenceDir.getBase(targetDir.getBrowser());
            LOG.info("基準エビデンスとして確定します {}", targetDir.getDir());
            copy(targetDir.getDir(), baseDir.getDir());

        }

    }

    void copy(File srcDir, File destDir) {

        for (File f1 : srcDir.listFiles()) {
            try {

                if (f1.getName().startsWith(COMPARE_PREFIX)
                        || (f1.isDirectory() && "base".equals(f1.getName()))) {
                    continue;
                }

                if (f1.isDirectory()) {
                    File f2 = new File(destDir.getPath(), f1.getName());
                    copy(f1, f2);
                } else {
                    FileUtils.copyFileToDirectory(f1, destDir);
                }

            } catch (IOException e) {
                LOG.error("エビデンスのコピー処理で例外が発生しました", e);
            }
        }

    }

}
