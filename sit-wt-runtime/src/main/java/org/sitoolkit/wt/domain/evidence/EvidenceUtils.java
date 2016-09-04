package org.sitoolkit.wt.domain.evidence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvidenceUtils {

    static final String BASE_EVIDENCE_ROOT = "base-evidence";

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceUtils.class);

    private static String buildDir = "target";
    private static String evidenceDirRegex = "^evidence_.*";

    public static File baseEvidenceDir(String browser) {
        return new File(BASE_EVIDENCE_ROOT, browser);

    }

    public static File targetEvidenceDir(String targetEvidenceDir) {
        if (targetEvidenceDir == null) {
            return getLatestEvidenceDir();
        } else {
            File targetEvidenceDirObj = new File(targetEvidenceDir);

            return targetEvidenceDirObj.exists() ? targetEvidenceDirObj : null;
        }
    }

    public static File getLatestEvidenceDir() {

        File outputDir = new File(buildDir);
        List<File> evidenceDirs = new ArrayList<File>(FileUtils.listFilesAndDirs(outputDir,
                FalseFileFilter.INSTANCE, new RegexFileFilter(evidenceDirRegex)));
        evidenceDirs.remove(outputDir);
        Collections.sort(evidenceDirs, new FileLastModifiedComarator(false));

        if (evidenceDirs.isEmpty()) {
            LOG.info("エビデンスフォルダがありません {}", outputDir.getAbsolutePath());
            return null;
        }

        return evidenceDirs.get(0);

    }

    public static class FileLastModifiedComarator implements Comparator<File> {

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

    public static String concatPath(String a, String b) {
        return a.endsWith("/") ? a + b : a + "/" + b;
    }

    public static String extractTable(File evidenceFile) throws IOException {

        List<String> lines = FileUtils.readLines(evidenceFile, "UTF-8");
        int tableTagStart = lines.indexOf("    <table class=\"table\">");
        int tableTagEnd = lines.indexOf("    </table>");

        if (tableTagStart == -1 || tableTagEnd == -1) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String line : lines.subList(tableTagStart, tableTagEnd + 1)) {
            sb.append(StringUtils.join("        ", line, "\n"));
        }

        return sb.toString();
    }
}
