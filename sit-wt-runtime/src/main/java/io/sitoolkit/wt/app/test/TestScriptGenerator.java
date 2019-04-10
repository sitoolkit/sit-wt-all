package io.sitoolkit.wt.app.test;

import java.nio.file.Path;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;

import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateModel;

public class TestScriptGenerator {

    @Resource
    private TemplateEngine templateEngine;

    public void generateNewScript(Path destFile) {
        String filename = destFile.getFileName().toString();
        String destFileBase = FilenameUtils.getBaseName(filename);
        String destFileExt = FilenameUtils.getExtension(filename);

        TemplateModel model = new TemplateModel();
        model.setTemplate("EmptyTestScript.vm");
        model.setOutDir(destFile.getParent().toString());
        model.setFileBase(destFileBase);
        model.setFileExt(destFileExt);

        Properties properties = new Properties();
        properties.putAll(MessageManager.getMessageMap("testScript-"));
        model.setProperties(properties);

        templateEngine.write(model);
    }

}
