package io.sitoolkit.wt.infra.template;

import java.nio.file.Path;
import java.util.Properties;
import javax.annotation.Resource;

public class MergedFileGenerator {

  @Resource
  private TemplateEngine templateEngine;

  public void generate(String templateBase, Path destDir, String destFileBase, String destFileExt,
      Properties properties) {
    TemplateModel model = new TemplateModel();
    model.setTemplate(templateBase + ".vm");
    model.setOutDir(destDir.toAbsolutePath().toString());
    model.setFileBase(destFileBase);
    model.setFileExt(destFileExt);
    model.setProperties(properties);

    templateEngine.write(model);
  }

}
