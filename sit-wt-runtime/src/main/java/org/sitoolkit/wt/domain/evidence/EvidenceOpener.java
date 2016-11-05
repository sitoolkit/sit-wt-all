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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvidenceOpener {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceOpener.class);
    private int openFileCount = 1;
    private String evidenceFileRegex = ".*\\.html$";

    public void open() {

        File evidenceDir = EvidenceDir.getLatestEvidenceDir();

        if (evidenceDir == null) {
            return;
        }

        List<File> opelogFiles = new ArrayList<File>(FileUtils.listFiles(evidenceDir,
                new RegexFileFilter(evidenceFileRegex), TrueFileFilter.INSTANCE));
        LOG.info("{}に{}のエビデンスがあります ", evidenceDir.getName(), opelogFiles.size());

        Collections.sort(opelogFiles, new FileLastModifiedComarator(true));

        try {
            int openFiles = 0;
            for (File file : opelogFiles) {
                Desktop.getDesktop().open(file);

                if (++openFiles >= openFileCount) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

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
