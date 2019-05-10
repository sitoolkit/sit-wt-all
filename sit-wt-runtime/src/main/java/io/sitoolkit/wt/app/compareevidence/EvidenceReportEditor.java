package io.sitoolkit.wt.app.compareevidence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.domain.evidence.EvidenceDir;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class EvidenceReportEditor {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(EvidenceReportEditor.class);

    private static final String evidenceRes = "js/report.js";

    private static final String reportResourcePath = "target/site";

    private static final String[] scriptTags = new String[] {
            "    <script src=\"../js/jquery.js\"></script>\n",
            "    <script src=\"js/report.js\"></script>\n" };

    public static void main(String[] args) {
        System.setProperty("sitwt.projectDirectory", "B:\\tools\\Git\\home\\java\\command");
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
            String reportHtml = FileUtils.readFileToString(failsafeReport.toFile(),
                    StandardCharsets.UTF_8);

            reportHtml = addScriptTag(reportHtml, scriptTags);

            FileUtils.writeStringToFile(failsafeReport.toFile(), reportHtml,
                    StandardCharsets.UTF_8);

        } catch (IOException e) {
            LOG.error("add.tags.error", e);
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

}
