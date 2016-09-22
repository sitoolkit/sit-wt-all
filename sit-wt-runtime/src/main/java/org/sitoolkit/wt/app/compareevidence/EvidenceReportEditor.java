package org.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.EvidenceUtils;
import org.sitoolkit.wt.domain.evidence.ReportOpener;
import org.sitoolkit.wt.infra.TestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class EvidenceReportEditor {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceReportEditor.class);

    private static final String evidenceRes = "js/report.js";

    private static final String reportResources = "target/site";

    private static final String failsafeReportName = "failsafe-report.html";

    private static final String COMPARE_PREFIX = "comp_";

    private static final String MASK_PREFIX = "mask_";

    private static final String COMPARE_NG_PREFIX = "comp_ng_";

    public static void main(String[] args) {
        EvidenceReportEditor editor = new EvidenceReportEditor();
        editor.attachEvidenceLink();
    }

    @Deprecated
    public void attachEvidenceLink() {
        File evidenceDir = EvidenceUtils.getLatestEvidenceDir();

        if (evidenceDir == null) {
            return;
        }

        // レポートおよび関連css, jsコピー
        copy(new File(reportResources), evidenceDir);

        // リンク変換jsコピー
        try {
            URL url = ResourceUtils.getURL("classpath:evidence/" + evidenceRes);
            File dstFile = new File(evidenceDir, evidenceRes);
            FileUtils.copyURLToFile(url, dstFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File failsafeReport = new File(
                    EvidenceUtils.concatPath(evidenceDir.getPath(), failsafeReportName));
            String html = FileUtils.readFileToString(failsafeReport, "UTF-8");

            // ※maven-surefire-report-plugin 2.19では不要（現在は2.17）
            html = StringUtils.replace(html, "${outputEncoding}", "UTF-8");

            // failsafe-reportの<head>タグへのリンク変換js埋め込み
            String scriptTag1 = "    <script src=\"js/jquery.js\"></script>\n";
            String scriptTag2 = "    <script src=\"js/report.js\"></script>\n";
            String searchString = "  </head>";
            String replacement = StringUtils.join(scriptTag1, scriptTag2, searchString);
            html = StringUtils.replace(html, searchString, replacement);

            // inputタグの埋め込み
            List<String> evidenceNames = new ArrayList<>();
            for (String str : evidenceDir.list()) {
                if (isEvidenceName(str)) {
                    evidenceNames.add(str);
                }
            }
            StringBuilder inputTagBuilder = new StringBuilder();
            for (String evidenceName : evidenceNames) {
                String testScriptName = evidenceName.substring(0, evidenceName.indexOf("."));
                String className = StringUtils.join(testScriptName, "IT");
                String methodName = "test".concat(StringUtils
                        .removeStart(evidenceName, testScriptName).replaceAll("[^0-9]", ""));

                String evidenceDirPath = evidenceDir.getPath();
                String maskEvidenceName = searchFile(MASK_PREFIX.concat(evidenceName),
                        evidenceDirPath);
                String compEvidenceName = searchFile(COMPARE_PREFIX.concat(evidenceName),
                        evidenceDirPath);
                String compMaskEvidenceName = searchFile(
                        StringUtils.join(COMPARE_PREFIX, MASK_PREFIX, evidenceName),
                        evidenceDirPath);
                String compNgEvidenceName = searchFile(COMPARE_NG_PREFIX.concat(evidenceName),
                        evidenceDirPath);
                String compNgMaskEvidenceName = searchFile(
                        StringUtils.join(COMPARE_NG_PREFIX, MASK_PREFIX, evidenceName),
                        evidenceDirPath);

                StringBuilder dataAttrBuilder = new StringBuilder();
                dataAttrBuilder.append(StringUtils.join("data-evidence='", evidenceName, "' "));
                dataAttrBuilder.append(StringUtils.join("data-class='", className, "' "));
                dataAttrBuilder.append(StringUtils.join("data-method='", methodName, "' "));
                dataAttrBuilder.append(StringUtils.join("data-mask='", maskEvidenceName, "' "));
                dataAttrBuilder.append(StringUtils.join("data-comp='", compEvidenceName, "' "));
                dataAttrBuilder
                        .append(StringUtils.join("data-compmask='", compMaskEvidenceName, "' "));
                dataAttrBuilder.append(StringUtils.join("data-compng='", compNgEvidenceName, "' "));
                dataAttrBuilder.append(
                        StringUtils.join("data-compngmask='", compNgMaskEvidenceName, "' "));

                inputTagBuilder.append(StringUtils.join("<input type='hidden' ",
                        dataAttrBuilder.toString(), "/>\n"));
            }

            searchString = "  </body>";
            replacement = StringUtils.join(inputTagBuilder.toString(), searchString);
            html = StringUtils.replace(html, searchString, replacement);

            FileUtils.writeStringToFile(failsafeReport, html, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        String evidenceFileRegex = COMPARE_NG_PREFIX.concat(".*\\.html$");

        List<File> opelogFiles = new ArrayList<File>(FileUtils.listFiles(evidenceDir,
                new RegexFileFilter(evidenceFileRegex), TrueFileFilter.INSTANCE));

        if (opelogFiles.size() > 0) {
            // EvidenceOpener opener = new EvidenceOpener();
            ReportOpener opener = new ReportOpener();
            opener.open();
            throw new TestException("基準エビデンスと一致しないスクリーンショットを持つエビデンス存在します " + opelogFiles);
        }

    }

    private String searchFile(String targetName, String targetDirPath) {

        File f = new File(EvidenceUtils.concatPath(targetDirPath, targetName));

        if (f.exists()) {
            return f.getName();
        }
        return "";
    }

    private boolean isEvidenceName(String str) {

        if (str.startsWith(COMPARE_PREFIX) || str.startsWith(MASK_PREFIX)) {
            return false;
        }

        String regex = "_.\\d+.html$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);

        return m.find() ? true : false;

    }

    void copy(File srcDir, File destDir) {

        for (File f1 : srcDir.listFiles()) {
            try {

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
