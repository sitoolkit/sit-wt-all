package io.sitoolkit.wt.app.evidence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.domain.evidence.EvidenceDir;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class EvidenceReportEditor {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(EvidenceReportEditor.class);

    private static final String evidenceRes = "js/report.js";

    private static final String reportResourcePath = "target/site";

    private static final String scriptTags = "    <script src=\"../js/jquery.js\"></script>\n"
            + "    <script src=\"" + evidenceRes + "\"></script>\n";

    public static void main(String[] args) {
        new EvidenceReportEditor().edit(EvidenceDir.getLatest());
    }

    public void edit(EvidenceDir evidenceDir) {

        if (!evidenceDir.exists()) {
            LOG.error("evidence.error");
        } else if (!EvidenceDir.existsReport(reportResourcePath)) {
            LOG.error("report.error");
        } else {

            try {
                FileUtils.copyDirectory(new File(reportResourcePath),
                        evidenceDir.getReportDir().toFile(), false);

                URL url = ResourceUtils.getURL("classpath:evidence/" + evidenceRes);
                File dstFile = evidenceDir.getReportDir().resolve(evidenceRes).toFile();
                FileUtils.copyURLToFile(url, dstFile);

            } catch (IOException e) {
                LOG.error("resource.copy.error", e);
                return;
            } catch (Exception exp) {
                LOG.error("proxy.error", exp);
                return;
            }

            addTags(evidenceDir);
        }

    }

    private void addTags(EvidenceDir evidenceDir) {

        Path failsafeReport = evidenceDir.getFailsafeReport();

        try {
            String[] lines = FileUtils
                    .readFileToString(failsafeReport.toFile(), StandardCharsets.UTF_8).split("\n");

            StringBuilder sb = new StringBuilder();

            for (String line : lines) {

                if (line.trim().equals("</body>")) {
                    sb.append(buildInputTags(evidenceDir));
                }

                sb.append(line + "\n");

                if (line.trim().equals("<head>")) {
                    sb.append(scriptTags);
                }

            }

            FileUtils.writeStringToFile(failsafeReport.toFile(), sb.toString(),
                    StandardCharsets.UTF_8);

        } catch (IOException e) {
            LOG.error("add.tags.error", e);
        }

    }

    private String buildInputTags(EvidenceDir evidenceDir) {
        StringBuilder sb = new StringBuilder();

        for (File evidenceFile : evidenceDir.getEvidenceFiles()) {
            sb.append(buildInputTag(evidenceDir, evidenceFile));
        }

        return sb.toString();
    }

    private String buildInputTag(EvidenceDir evidenceDir, File evidenceFile) {

        String evidenceName = evidenceFile.getName();
        String testMethodFullName = FilenameUtils.getBaseName(evidenceName);

        StringBuilder sb = new StringBuilder();
        sb.append(buildAttribute("data-name", testMethodFullName));

        sb.append(buildAttribute("data-evidence", relativizePath(evidenceDir, evidenceFile)));
        sb.append(buildAttribute("data-mask",
                fetchPath(evidenceDir, evidenceDir.getMaskEvidence(evidenceName))));
        sb.append(buildAttribute("data-comp",
                fetchPath(evidenceDir, evidenceDir.getCompareEvidence(evidenceName))));
        sb.append(buildAttribute("data-compmask",
                fetchPath(evidenceDir, evidenceDir.getCompareMaskEvidence(evidenceName))));
        sb.append(buildAttribute("data-compng",
                fetchPath(evidenceDir, evidenceDir.getCompareNgEvidence(evidenceName))));

        return StringUtils.join("<input class='evidence' type='hidden' ", sb.toString(), "/>\n");
    }

    private String buildAttribute(String name, String value) {
        return name + "='" + value + "' ";
    }

    private String fetchPath(EvidenceDir evidenceDir, File targetFile) {
        return targetFile.exists() ? relativizePath(evidenceDir, targetFile) : "";
    }

    private String relativizePath(EvidenceDir evidenceDir, File targetFile) {
        return evidenceDir.getReportDir().relativize(targetFile.toPath()).toString();
    }

}
