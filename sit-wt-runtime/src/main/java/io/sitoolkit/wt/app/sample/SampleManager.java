package io.sitoolkit.wt.app.sample;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import io.sitoolkit.wt.infra.PropertyUtils;
import io.sitoolkit.wt.infra.SitLocaleUtils;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.infra.template.MergedFileGenerator;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;

public class SampleManager {

  private String projectDir = null;

  private static final String SAMPLE_RESOURCE_DIR = "sample/";
  private static final String MESSAGE_RESOURCE_DIR = "io/sitoolkit/wt/infra/resource/";

  @Resource
  private MergedFileGenerator mergedFileGenerator;

  public static void staticUnarchiveBasicSample() {
    try (AnnotationConfigApplicationContext appCtx =
        new AnnotationConfigApplicationContext(SampleManagerConfig.class)) {

      appCtx.getBean(SampleManager.class).unarchiveBasicSample();
    }
  }

  public void unarchiveBasicSample(String projectDir) {
    this.projectDir = projectDir;
    unarchiveBasicSample();
  }

  private void unarchiveBasicSample() {
    unarchive("bootstrap.min.css");
    unarchive("pom.xml");

    Map<String, String> properties = loadProperties();

    generateLocalizedHtml("input", properties);
    generateLocalizedHtml("terms", properties);
    generateLocalizedHtml("done", properties);

    properties.putAll(MessageManager.getResourceAsMap());

    mergedFileGenerator.generate(SAMPLE_RESOURCE_DIR + "SampleTestScript",
        getDestPath("testscript"), "SampleTestScript", "csv", properties);
  }

  private void unarchive(String filename) {
    String resource = SAMPLE_RESOURCE_DIR + filename;
    FileIOUtils.sysRes2file(resource, getDestPath(resource));
  }

  private void generateLocalizedHtml(String fileBase, Map<String, String> properties) {
    mergedFileGenerator.generate(SAMPLE_RESOURCE_DIR + fileBase, getDestPath(SAMPLE_RESOURCE_DIR),
        fileBase, "html", properties);
  }

  private Map<String, String> loadProperties() {
    String resourcePath = "/" + MESSAGE_RESOURCE_DIR + getPropertiesFileName();
    return PropertyUtils.loadAsMap(resourcePath, false);
  }

  private String getPropertiesFileName() {
    String fileName;
    String baceName = "message";
    if (SitLocaleUtils.defaultLanguageEquals(Locale.JAPANESE)) {
      fileName = baceName + "_" + Locale.JAPANESE.getLanguage() + "_JP";
    } else {
      fileName = baceName;
    }
    return fileName + ".properties";
  }

  private Path getDestPath(String path) {
    return (projectDir == null) ? Paths.get(path) : Paths.get(projectDir, path);
  }

  public static void main(String[] args) {
    staticUnarchiveBasicSample();
  }
}
