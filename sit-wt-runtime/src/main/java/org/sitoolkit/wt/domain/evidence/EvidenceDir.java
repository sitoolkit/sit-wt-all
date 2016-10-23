package org.sitoolkit.wt.domain.evidence;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.infra.PropertyUtils;

public class EvidenceDir {

    static final String BASE_EVIDENCE_ROOT = "base-evidence";

    static final String COMPARE_PREFIX = "comp_";

    static final String COMPARE_NG_PREFIX = "comp_ng_";

    static final String MASK_PREFIX = "mask_";

    static final String UNMATCH_PREFIX = "unmatch_";

    static final String FAILSAFE_REPORT_NAME = "failsafe-report.html";

    static final String PROJECT_REPORTS_NAME = "project-reports.html";

    private File dir;

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

    public static EvidenceDir getLatest() {
        return getInstance(EvidenceUtils.getLatestEvidenceDir());
    }

    public List<File> getEvidenceFiles() {
        List<File> evidenceFiles = new ArrayList<>();

        for (File evidenceFile : FileUtils.listFiles(dir, new String[] { "html" }, true)) {

            String htmlName = evidenceFile.getName();
            if (htmlName.startsWith(COMPARE_PREFIX) || htmlName.startsWith(MASK_PREFIX)
                    || htmlName.equals(FAILSAFE_REPORT_NAME)
                    || htmlName.equals(PROJECT_REPORTS_NAME)) {
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

        String imgPrefix = StringUtils.removeEnd(evidenceFileName, ".html");
        String maskPrefix = MASK_PREFIX + imgPrefix;
        String unmatchPrefix = UNMATCH_PREFIX + imgPrefix;
        String unmatchMaskPrefix = UNMATCH_PREFIX + MASK_PREFIX + imgPrefix;

        for (File imgFile : FileUtils.listFiles(dir, new String[] { "png" }, true)) {

            String imgName = imgFile.getName();
            if (imgName.startsWith(imgPrefix) || imgName.startsWith(maskPrefix)
                    || imgName.startsWith(unmatchPrefix) || imgName.startsWith(unmatchMaskPrefix)) {
                screenshotFiles.put(imgFile.getName(), imgFile);
            }
        }

        return screenshotFiles;
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

}
