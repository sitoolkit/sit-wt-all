package org.sitoolkit.wt.domain.evidence;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;

public class EvidenceOpener {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(EvidenceOpener.class);
    private int openFileCount = 1;
    private String evidenceFileRegex = ".*\\.html$";
    private String maskFileRegex = "^mask_.*html$";
    private String compareFileRegex = "^comp_(?!mask_|ng_).*html$";
    private String compareNgFileRegex = "^comp_ng_.*html$";

    public void open() {
        openFiles(null, evidenceFileRegex, "");
    }

    public void openCompareEvidence(EvidenceDir targetDir) {
        openFiles(targetDir, compareFileRegex, "比較");
    }

    public void openCompareNgEvidence(EvidenceDir targetDir) {
        openFiles(targetDir, compareNgFileRegex, "比較NG");
    }

    public void openMaskEvidence(EvidenceDir targetDir) {
        openFiles(targetDir, maskFileRegex, "マスク");
    }

    public void openFiles(EvidenceDir targetDir, String targetFileRegex, String evidenceType) {

        File evidenceDir = targetDir == null ? EvidenceDir.getLatestEvidenceDir()
                : targetDir.getDir();

        if (evidenceDir == null) {
            return;
        }

        List<File> targetFiles = new ArrayList<File>(FileUtils.listFiles(evidenceDir,
                new RegexFileFilter(targetFileRegex), TrueFileFilter.INSTANCE));
        LOG.info("evidence.info", evidenceDir.getName(), targetFiles.size(), evidenceType);

        Collections.sort(targetFiles, new FileLastModifiedComarator(true));

        try {
            int openFiles = 0;
            for (File file : targetFiles) {
                Desktop.getDesktop().open(file);

                if (++openFiles >= openFileCount) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    public void openTarget(File targetFile) {
        evidenceFileRegex = targetFile.getName() + ".*\\.html$";
        open();
    }

    public class FileLastModifiedComarator implements Comparator<File> {

        private int signum = 1;

        public FileLastModifiedComarator() {
            super();
        }

        public FileLastModifiedComarator(boolean ascending) {
            this();
            signum = ascending ? 1 : -1;
        }

        @Override
        public int compare(File o1, File o2) {
            return signum * (int) (o1.lastModified() - o2.lastModified());
        }
    }

    public static void main(String[] args) {
        EvidenceOpener opener = new EvidenceOpener();
        opener.open();
    }
}
