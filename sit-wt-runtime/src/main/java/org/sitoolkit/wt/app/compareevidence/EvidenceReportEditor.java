package org.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class EvidenceReportEditor {

    private static final Logger LOG = LoggerFactory.getLogger(EvidenceReportEditor.class);

    private static final String evidenceRes = "js/report.js";

    private static final String reportResourcePath = "target/site";

    private static final String FAILSAFE_REPORT_NAME = "failsafe-report.html";

    private static final String MASK_PREFIX = "mask_";

    private static final String COMPARE_PREFIX = "comp_";

    private static final String COMPARE_NG_PREFIX = "comp_ng_";

    private static final String[] scriptTags = new String[] {
            "    <script src=\"js/jquery.js\"></script>\n",
            "    <script src=\"js/report.js\"></script>\n" };

    public void edit() {

        EvidenceDir evidenceDir = EvidenceDir.getLatest();

        if (!evidenceDir.exists()) {
            LOG.info("エビデンスがありません");
        } else if (!new File(reportResourcePath, FAILSAFE_REPORT_NAME).exists()) {
            LOG.info("レポートファイルがありません");
        } else {

            try {
                FileUtils.copyDirectory(new File(reportResourcePath), evidenceDir.getDir());

                URL url = ResourceUtils.getURL("classpath:evidence/" + evidenceRes);
                File dstFile = new File(evidenceDir.getDir(), evidenceRes);
                FileUtils.copyURLToFile(url, dstFile);

            } catch (IOException e) {
                LOG.error("リソースのコピーに失敗しました", e);
            }

            addTags(evidenceDir);
        }

    }

    private void addTags(EvidenceDir evidenceDir) {

        File failsafeReport = new File(evidenceDir.getDir(), FAILSAFE_REPORT_NAME);

        try {
            String reportHtml = FileUtils.readFileToString(failsafeReport, "UTF-8");

            reportHtml = addScriptTag(reportHtml, scriptTags);

            StringBuilder sb = new StringBuilder();
            for (File evidenceFile : evidenceDir.getEvidenceFiles()) {
                sb.append(buildInputTag(evidenceDir, evidenceFile.getName()));
            }
            reportHtml = addInputTag(reportHtml, sb.toString());

            FileUtils.writeStringToFile(failsafeReport, reportHtml, "UTF-8");

        } catch (IOException e) {
            LOG.error("リンク付加処理で例外が発生しました", e);
        }

    }

    private String addScriptTag(String reportHtml, String... tags) throws IOException {

        String[] lines = reportHtml.split("\n");

        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            sb.append(line + "\n");
            if (line.trim().equals("<head>")) {
                for (String tag : tags) {
                    sb.append(tag);
                }
            }
        }

        return sb.toString();
    }

    private String buildInputTag(EvidenceDir evidenceDir, String evidenceName) {

        String testScriptName = evidenceName.substring(0, evidenceName.indexOf("."));
        String className = StringUtils.join(testScriptName, "IT");
        String methodName = "test"
                + StringUtils.removeStart(evidenceName, testScriptName).replaceAll("[^0-9]", "");

        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.join("data-evidence='", evidenceName, "' "));
        sb.append(StringUtils.join("data-class='", className, "' "));
        sb.append(StringUtils.join("data-method='", methodName, "' "));

        sb.append(StringUtils.join("data-mask='",
                fetchName(new File(evidenceDir.getDir().getPath(), MASK_PREFIX + evidenceName)),
                "' "));
        sb.append(StringUtils.join("data-comp='",
                fetchName(new File(evidenceDir.getDir().getPath(), COMPARE_PREFIX + evidenceName)),
                "' "));
        sb.append(
                StringUtils.join("data-compmask='",
                        fetchName(new File(evidenceDir.getDir().getPath(),
                                StringUtils.join(COMPARE_PREFIX, MASK_PREFIX, evidenceName))),
                        "' "));
        sb.append(StringUtils.join("data-compng='",
                fetchName(
                        new File(evidenceDir.getDir().getPath(), COMPARE_NG_PREFIX + evidenceName)),
                "' "));

        return StringUtils.join("<input type='hidden' ", sb.toString(), "/>\n");
    }

    private String fetchName(File targetFile) {
        return targetFile.exists() ? targetFile.getName() : "";
    }

    private String addInputTag(String reportHtml, String inputHidden) {

        String[] lines = reportHtml.split("\n");

        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            if (line.trim().equals("</body>")) {
                sb.append(inputHidden);
            }
            sb.append(line + "\n");
        }

        return sb.toString();
    }

}
