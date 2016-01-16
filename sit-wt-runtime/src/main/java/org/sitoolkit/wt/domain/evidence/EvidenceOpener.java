package org.sitoolkit.wt.domain.evidence;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.PatternFilenameFilter;

public class EvidenceOpener {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceOpener.class);
    private int openFileCount = 1;
    private String buildDir = "target";
    private String opelogDirRegex = "^opelog_.*";
    private String opelogFileRegex = ".*\\.html$";

    public void open() {
        File outputDir = new File(buildDir);
        File[] opelogDirs = outputDir.listFiles(new PatternFilenameFilter(opelogDirRegex));
        Arrays.sort(opelogDirs, new FileLastModifiedComarator(false));

        if (opelogDirs.length == 0) {
            LOG.info("エビデンスフォルダがありません {}", outputDir.getAbsolutePath());
            return;
        }

        File[] opelogFiles = opelogDirs[0].listFiles(new PatternFilenameFilter(opelogFileRegex));
        LOG.info("{}に{}のエビデンスがあります ", opelogDirs[0].getName(), opelogFiles.length);

        Arrays.sort(opelogFiles, new FileLastModifiedComarator(true));

        try {
            for (int i = 0; i < openFileCount; i++) {
                Desktop.getDesktop().open(opelogFiles[i]);
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
