package io.sitoolkit.wt.app.evidence;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import io.sitoolkit.wt.domain.evidence.EvidenceDir;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.infra.template.MergedFileGenerator;

public class EvidenceReportEditor {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(EvidenceReportEditor.class);

  private static final String EVIDENCE_RESOURCE_DIR = "evidence/";

  private static final String SCRIPT_DIR = "js/";

  private static final String SCRIPT_BASENAME = "report";

  private static final String SCRIPT_EXTENTION = "js";

  private static final String REPORT_RESOURCE_PATH = "target/site";

  private static final String ADDITIONAL_SCRIPT_TAGS =
      "    <script src=\"../js/jquery.js\"></script>\n"
          + "    <script src=\""
          + SCRIPT_DIR
          + SCRIPT_BASENAME
          + "."
          + SCRIPT_EXTENTION
          + "\"></script>\n";

  @Resource private MergedFileGenerator mergedFileGenerator;

  public static void main(String[] args) {
    staticExecute(EvidenceDir.getLatest());
  }

  public static void staticExecute(EvidenceDir evidenceDir) {
    try (AnnotationConfigApplicationContext appCtx =
        new AnnotationConfigApplicationContext(EvidenceReportEditorConfig.class)) {

      appCtx.getBean(EvidenceReportEditor.class).edit(evidenceDir);
    }
  }

  public static void staticExecute(EvidenceDir evidenceDir, String resourceDir) {
    try (AnnotationConfigApplicationContext appCtx =
        new AnnotationConfigApplicationContext(EvidenceReportEditorConfig.class)) {

      appCtx.getBean(EvidenceReportEditor.class).edit(evidenceDir, resourceDir);
    }
  }

  public void edit(EvidenceDir evidenceDir) {
    edit(evidenceDir, REPORT_RESOURCE_PATH);
  }

  public void edit(EvidenceDir evidenceDir, String resourceDir) {

    if (!evidenceDir.exists()) {
      LOG.error("evidence.error");
      return;
    }

    if (!EvidenceDir.existsReport(resourceDir)) {
      LOG.error("report.error");
      return;
    }

    try {
      FileUtils.copyDirectory(new File(resourceDir), evidenceDir.getReportDir().toFile(), false);
    } catch (IOException e) {
      LOG.error("resource.copy.error", e);
      return;
    }

    generateReportScript(evidenceDir);

    addTags(evidenceDir);
  }

  private void generateReportScript(EvidenceDir evidenceDir) {
    String resourceBase = EVIDENCE_RESOURCE_DIR + SCRIPT_DIR + SCRIPT_BASENAME;
    Map<String, String> properties = MessageManager.getResourceAsMap();

    Path destDir = evidenceDir.getReportDir().resolve(SCRIPT_DIR);
    mergedFileGenerator.generate(
        resourceBase, destDir, SCRIPT_BASENAME, SCRIPT_EXTENTION, properties);
  }

  private void addTags(EvidenceDir evidenceDir) {

    Path failsafeReport = evidenceDir.getFailsafeReport();

    try {
      String[] lines =
          FileUtils.readFileToString(failsafeReport.toFile(), StandardCharsets.UTF_8).split("\n");

      StringBuilder sb = new StringBuilder();

      for (String line : lines) {

        String trimmed = line.trim();

        if (trimmed.equals("</body>")) {
          sb.append(buildInputTags(evidenceDir));
        }

        sb.append(line + "\n");

        if (trimmed.equals("<head>")) {
          sb.append(ADDITIONAL_SCRIPT_TAGS);
        }
      }

      FileUtils.writeStringToFile(failsafeReport.toFile(), sb.toString(), StandardCharsets.UTF_8);

    } catch (IOException e) {
      LOG.error("add.tags.error", e);
    }
  }

  private String buildInputTags(EvidenceDir evidenceDir) {
    StringBuilder sb = new StringBuilder();

    for (File evidenceFile : evidenceDir.getEvidenceFiles()) {
      sb.append(buildInputTag(evidenceDir, evidenceFile.toPath()));
    }

    return sb.toString();
  }

  private String buildInputTag(EvidenceDir evidenceDir, Path evidenceFile) {

    String evidenceName = evidenceFile.getFileName().toString();
    String testMethodFullName = FilenameUtils.getBaseName(evidenceName);

    StringBuilder sb = new StringBuilder();
    sb.append(buildAttribute("data-name", testMethodFullName));

    sb.append(buildAttribute("data-evidence", relativizePath(evidenceDir, evidenceFile)));
    sb.append(
        buildAttribute(
            "data-mask", fetchPath(evidenceDir, evidenceDir.getMaskEvidence(evidenceName))));
    sb.append(
        buildAttribute(
            "data-comp", fetchPath(evidenceDir, evidenceDir.getCompareEvidence(evidenceName))));
    sb.append(
        buildAttribute(
            "data-compmask",
            fetchPath(evidenceDir, evidenceDir.getCompareMaskEvidence(evidenceName))));
    sb.append(
        buildAttribute(
            "data-compng", fetchPath(evidenceDir, evidenceDir.getCompareNgEvidence(evidenceName))));

    return StringUtils.join("<input class='evidence' type='hidden' ", sb.toString(), "/>\n");
  }

  private String buildAttribute(String name, String value) {
    return name + "='" + value + "' ";
  }

  private String fetchPath(EvidenceDir evidenceDir, Path target) {
    return target.toFile().exists() ? relativizePath(evidenceDir, target) : "";
  }

  private String relativizePath(EvidenceDir evidenceDir, Path target) {
    return evidenceDir.getReportDir().relativize(target).toString();
  }
}
