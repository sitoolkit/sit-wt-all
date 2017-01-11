package org.sitoolkit.wt.domain.evidence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.infra.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvidenceDir {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceDir.class);

    private static final String BASE_EVIDENCE_ROOT = "base-evidence";

    private static final String COMPARE_PREFIX = "comp_";

    private static final String COMPARE_NG_PREFIX = "comp_ng_";

    private static final String MASK_PREFIX = "mask_";

    private static final String UNMATCH_PREFIX = "unmatch_";

    private static final String FAILSAFE_REPORT_NAME = "failsafe-report.html";

    private static final String PROJECT_REPORTS_NAME = "project-reports.html";

    private static final String IMG_BASE_DIR = "base";

    private static final String EVIDENCE_ROOT_DIR = "evidence";

    private File dir;

    private static String evidenceDirRegex = "^evidence_.*";

    private EvidenceDir() {
    }

    public static EvidenceDir getInstance(File dir) {
        EvidenceDir instance = new EvidenceDir();
        instance.dir = dir;
        return instance;
    }

    public static EvidenceDir getInstance(String dir) {
        return getInstance(new File(dir));
    }

    public static EvidenceDir getBase(String browser) {
        return getInstance(new File(BASE_EVIDENCE_ROOT, browser));
    }

    public static String getRoot() {
        return EVIDENCE_ROOT_DIR;
    }

    public static EvidenceDir getLatest() {
        return getInstance(getLatestEvidenceDir());
    }

    public static File getLatestEvidenceDir() {

        File outputDir = new File(EVIDENCE_ROOT_DIR);
        List<File> evidenceDirs = new ArrayList<File>(FileUtils.listFilesAndDirs(outputDir,
                FalseFileFilter.INSTANCE, new RegexFileFilter(evidenceDirRegex)));
        evidenceDirs.remove(outputDir);
        Collections.sort(evidenceDirs, new FileNameComarator(false));

        if (evidenceDirs.isEmpty()) {
            LOG.info("エビデンスフォルダがありません {}", outputDir.getAbsolutePath());
            return null;
        }

        return evidenceDirs.get(0);

    }

    public static class FileNameComarator implements Comparator<File> {

        private int signum = 1;

        public FileNameComarator() {
            super();
        }

        public FileNameComarator(boolean ascending) {
            this();
            signum = ascending ? 1 : -1;
        }

        @Override
        public int compare(File o1, File o2) {
            return signum * o1.compareTo(o2);
        }
    }

    public static EvidenceDir targetEvidenceDir(String dir) {
        return dir == null ? EvidenceDir.getLatest() : EvidenceDir.getInstance(dir);
    }

    public static EvidenceDir baseEvidenceDir(String dir, String browser) {
        return dir == null ? EvidenceDir.getBase(browser) : EvidenceDir.getInstance(dir);
    }

    public List<File> getEvidenceFiles() {
        List<File> evidenceFiles = new ArrayList<>();

        for (File evidenceFile : FileUtils.listFiles(dir, new String[] { "html" }, true)) {

            String htmlName = evidenceFile.getName();
            if (isCompareEvidence(htmlName) || isCompareNgEvidence(htmlName)
                    || isMaskEvidence(htmlName) || isFailsafeReport(htmlName)
                    || isProjectReport(htmlName)) {
                continue;
            }

            evidenceFiles.add(evidenceFile);
        }

        return evidenceFiles;
    }

    public Collection<File> getScreenshots(String evidenceFileName) {
        return getScreenshotFilesAsMap(evidenceFileName).values();
    }

    public Map<String, File> getScreenshotFilesAsMap(String evidenceFileName) {

        Map<String, File> screenshotFiles = new HashMap<>();

        String evidenceName = StringUtils.removeEnd(evidenceFileName, ".html");

        for (File imgFile : FileUtils.listFiles(dir, new String[] { "png" }, true)) {

            String imgName = imgFile.getName();
            if (isEvidenceScreenshot(imgName, evidenceName)
                    || isMaskEvidenceScreenshot(imgName, evidenceName)
                    || isUnmatchEvidenceScreenshot(imgName, evidenceName)
                    || isUnmatchMaskEvidenceScreenshot(imgName, evidenceName)) {
                screenshotFiles.put(imgFile.getName(), imgFile);
            }
        }

        return screenshotFiles;
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

    public static String removeInputLine(String htmlString) throws IOException {

        String[] lines = htmlString.split("\n");

        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            if (line.trim().startsWith("<input")) {
                continue;
            }
            sb.append(line + "\n");
        }

        return sb.toString();

    }

    public static String replaceImgPath(String str) {
        return StringUtils.replace(str, "src=\"img", "src=\"img/" + IMG_BASE_DIR);
    }

    public static String toMaskEvidenceName(String name) {
        return concat(MASK_PREFIX, name);
    }

    public static String toCompareMaskEvidenceName(String name) {
        return concat(COMPARE_PREFIX + MASK_PREFIX, name);
    }

    public static String toMaskSsName(String name) {
        return concat(MASK_PREFIX, name);
    }

    public static String toUnmatchSsName(String name) {
        return concat(UNMATCH_PREFIX, name);
    }

    public static String toUnmatchMaskSsName(String name) {
        return concat(UNMATCH_PREFIX + MASK_PREFIX, name);
    }

    private static String concat(String prefix, String str) {
        return prefix + str;
    }

    public static String toBeforeMaskSsName(String name) {
        return StringUtils.removeStart(name, MASK_PREFIX);
    }

    public static boolean isBaseImgDir(File file) {
        return file.isDirectory() && IMG_BASE_DIR.equals(file.getName());
    }

    public static boolean existsReport(String resourcePath) {
        return new File(resourcePath, FAILSAFE_REPORT_NAME).exists();
    }

    public static String getCompareEvidencePrefix(boolean withUnmatch) {
        return withUnmatch ? COMPARE_NG_PREFIX : COMPARE_PREFIX;
    }

    public static boolean isFailsafeReport(String name) {
        return StringUtils.equals(name, FAILSAFE_REPORT_NAME);
    }

    public static boolean isProjectReport(String name) {
        return StringUtils.equals(name, PROJECT_REPORTS_NAME);
    }

    public static boolean isCompareEvidence(String name) {
        return startsWith(name, COMPARE_PREFIX);
    }

    public static boolean isCompareNgEvidence(String name) {
        return startsWith(name, COMPARE_NG_PREFIX);
    }

    public static boolean isMaskEvidence(String name) {
        return startsWith(name, MASK_PREFIX);
    }

    public static boolean isEvidenceScreenshot(String ssName, String evidenceName) {
        return startsWith(ssName, evidenceName);
    }

    public static boolean isMaskEvidenceScreenshot(String name, String evidenceName) {
        return startsWith(name, MASK_PREFIX + evidenceName);
    }

    public static boolean isUnmatchEvidenceScreenshot(String ssName, String evidenceName) {
        return startsWith(ssName, UNMATCH_PREFIX + evidenceName);
    }

    public static boolean isUnmatchMaskEvidenceScreenshot(String ssName, String evidenceName) {
        return startsWith(ssName, UNMATCH_PREFIX + MASK_PREFIX + evidenceName);
    }

    public static boolean isMaskScreenshot(String name) {
        return startsWith(name, MASK_PREFIX);
    }

    public static boolean isUnmatchScreenshot(String name) {
        return startsWith(name, UNMATCH_PREFIX);
    }

    public static boolean isUnmatchMaskScreenshot(String name) {
        return startsWith(name, UNMATCH_PREFIX + MASK_PREFIX);
    }

    private static boolean startsWith(String name, String prefix) {
        return StringUtils.startsWith(name, prefix);
    }

    public boolean exists() {
        return dir != null && dir.exists();
    }

    /**
     * 当該エビデンスの作成に使用されたブラウザを取得します。
     *
     * @return 当該エビデンスの作成に使用されたブラウザ
     */
    public String getBrowser() {
        Properties prop = PropertyUtils
                .loadFromPathWithCache(new File(dir, "sit-wt.properties").getAbsolutePath());
        return prop.getProperty("driverType");
    }

    public File getDir() {
        return dir;
    }

    public File getFailsafeReport() {
        return new File(dir.getPath(), FAILSAFE_REPORT_NAME);
    }

    public File getImgBaseDir() {
        return new File(StringUtils.join(new String[] { dir.getPath(), "img", IMG_BASE_DIR }, "/"));
    }

    public File getMaskEvidence(String name) {
        return getEvidenceWithPrefix(MASK_PREFIX, name);
    }

    public File getCompareEvidence(String name) {
        return getEvidenceWithPrefix(COMPARE_PREFIX, name);
    }

    public File getCompareMaskEvidence(String name) {
        return getEvidenceWithPrefix(COMPARE_PREFIX + MASK_PREFIX, name);
    }

    public File getCompareNgEvidence(String name) {
        return getEvidenceWithPrefix(COMPARE_NG_PREFIX, name);
    }

    private File getEvidenceWithPrefix(String prefix, String name) {
        return new File(dir.getPath(), prefix + name);
    }

}
