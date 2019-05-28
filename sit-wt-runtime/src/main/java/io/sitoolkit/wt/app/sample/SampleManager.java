package io.sitoolkit.wt.app.sample;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import io.sitoolkit.wt.app.template.TemplateConfig;
import io.sitoolkit.wt.infra.PropertyUtils;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.infra.template.LocalizedFileGenerator;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;

public class SampleManager {

  private String projectDir = null;

  private static final String RESOURCE_DIR = "sample/";

  private LocalizedFileGenerator localizedFileGenerator;

  public SampleManager() {
    try (AnnotationConfigApplicationContext appCtx =
        new AnnotationConfigApplicationContext(TemplateConfig.class)) {
      localizedFileGenerator = appCtx.getBean(LocalizedFileGenerator.class);
    }
  }

  public void unarchiveBasicSample(String projectDir) {
    this.projectDir = projectDir;
    unarchiveBasicSample();
  }

  public void unarchiveBasicSample() {
    unarchive("bootstrap.min.css");
    unarchive("pom.xml");

    Properties inputProperties = generateLocalizedHtml("input");
    Properties termsProperties = generateLocalizedHtml("terms");
    Properties doneProperties = generateLocalizedHtml("done");

    Properties scriptProperties = loadProperties("SampleTestScript");
    scriptProperties.putAll(inputProperties);
    scriptProperties.putAll(termsProperties);
    scriptProperties.putAll(doneProperties);
    scriptProperties.putAll(MessageManager.getMessageMap("testScript-"));

    localizedFileGenerator.generate(RESOURCE_DIR + "SampleTestScript.vm", getDestPath("testscript"),
        "SampleTestScript", "csv", scriptProperties);
  }

  private void unarchive(String filename) {
    String resource = RESOURCE_DIR + filename;
    FileIOUtils.sysRes2file(resource, getDestPath(resource));
  }

  private Properties generateLocalizedHtml(String fileBase) {
    Properties properties = loadProperties(fileBase);

    localizedFileGenerator.generate(RESOURCE_DIR + fileBase + ".vm", getDestPath(RESOURCE_DIR),
        fileBase, "html", properties);

    return properties;
  }

  private Properties loadProperties(String fileBase) {
    String resourcePath = "/" + RESOURCE_DIR + fileBase;
    return PropertyUtils.loadLocalizedProperties(resourcePath, false);
  }

  private Path getDestPath(String path) {
    return (projectDir == null) ? Paths.get(path) : Paths.get(projectDir, path);
  }

  public static void main(String[] args) {
    new SampleManager().unarchiveBasicSample();
  }
}
