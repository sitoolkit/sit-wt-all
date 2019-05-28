package io.sitoolkit.wt.infra.template;

import java.nio.file.Path;
import java.util.Properties;
import javax.annotation.Resource;

public class LocalizedFileGenerator {

  @Resource
  private TemplateEngine templateEngine;

  public void generate(String template, Path destDir, String destFileBase, String destFileExt,
      Properties properties) {
    TemplateModel model = new TemplateModel();
    model.setTemplate(template);
    model.setOutDir(destDir.toAbsolutePath().toString());
    model.setFileBase(destFileBase);
    model.setFileExt(destFileExt);
    model.setProperties(properties);

    templateEngine.write(model);
  }

}
