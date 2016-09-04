package org.sitoolkit.wt.domain.evidence;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseEvidenceManager {

    private static final Logger LOG = LoggerFactory.getLogger(BaseEvidenceManager.class);

    private static final String COMPARE_PREFIX = "comp_";

    public static void main(String[] args) {
        BaseEvidenceManager manager = new BaseEvidenceManager();
        manager.setBaseEvidence(null, "default");
    }

    /**
     * 指定されたエビデンスを基準エビデンスとして確定します。
     *
     *
     * @param targetEvidenceDir
     *            基準として確定するエビデンスのディレクトリ
     * @param browser
     *            対象エビデンスのテスト実行に使用したブラウザ
     * @see EvidenceUtils#baseEvidenceDir(String)
     */
    public void setBaseEvidence(String targetEvidenceDir, String browser) {

        File targetEvidenceDirObj = EvidenceUtils.targetEvidenceDir(targetEvidenceDir);

        if (targetEvidenceDirObj == null) {
            LOG.info("エビデンスがありません");
        } else {
            File baseEvidenceDir = EvidenceUtils.baseEvidenceDir(browser);
            LOG.info("エビデンス{}を基準エビデンスとして確定します {}", targetEvidenceDirObj, baseEvidenceDir);
            copy(targetEvidenceDirObj, baseEvidenceDir);

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
                    File f2 = new File(EvidenceUtils.concatPath(destDir.getPath(), f1.getName()));
                    copy(f1, f2);
                } else {
                    FileUtils.copyFileToDirectory(f1, destDir);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
