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

  private static final String RESOURCE_DIR = "sample/";

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

    Map<String, String> inputProperties = generateLocalizedHtml("input");
    Map<String, String> termsProperties = generateLocalizedHtml("terms");
    Map<String, String> doneProperties = generateLocalizedHtml("done");

    Map<String, String> scriptProperties = loadProperties("SampleTestScript");
    scriptProperties.putAll(inputProperties);
    scriptProperties.putAll(termsProperties);
    scriptProperties.putAll(doneProperties);
    scriptProperties.putAll(MessageManager.getResourceAsMap());

    mergedFileGenerator.generate(RESOURCE_DIR + "SampleTestScript", getDestPath("testscript"),
        "SampleTestScript", "csv", scriptProperties);
  }

  private void unarchive(String filename) {
    String resource = RESOURCE_DIR + filename;
    FileIOUtils.sysRes2file(resource, getDestPath(resource));
  }

  private Map<String, String> generateLocalizedHtml(String fileBase) {
    Map<String, String> properties = loadProperties(fileBase);

    mergedFileGenerator.generate(RESOURCE_DIR + fileBase, getDestPath(RESOURCE_DIR), fileBase,
        "html", properties);

    return properties;
  }

  private Map<String, String> loadProperties(String fileBase) {
    String resourcePath = "/" + RESOURCE_DIR + getPropertiesFileName(fileBase);
    return PropertyUtils.loadAsMap(resourcePath, false);
  }

  private String getPropertiesFileName(String name) {
    String fileName;
    if (SitLocaleUtils.defaultLanguageEquals(Locale.JAPANESE)) {
      fileName = name + "_" + Locale.JAPANESE.getLanguage();
    } else {
      fileName = name;
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
