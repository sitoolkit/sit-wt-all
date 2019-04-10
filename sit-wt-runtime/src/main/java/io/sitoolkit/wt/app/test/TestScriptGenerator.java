package io.sitoolkit.wt.app.test;

import java.io.File;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;

import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateModel;

public class TestScriptGenerator {

    @Resource
    private TemplateEngine templateEngine;

    public void generateNewScript(File destFile) {
        String destFileBase = FilenameUtils.getBaseName(destFile.getName());
        String destFileExt = FilenameUtils.getExtension(destFile.getName());

        TemplateModel model = new TemplateModel();
        model.setTemplate("EmptyTestScript.vm");
        model.setOutDir(destFile.getParent());
        model.setFileBase(destFileBase);
        model.setFileExt(destFileExt);

        Properties properties = new Properties();
        properties.putAll(MessageManager.getMessageMap("testScript-"));
        model.setProperties(properties);

        templateEngine.write(model);
    }

}
