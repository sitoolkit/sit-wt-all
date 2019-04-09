package io.sitoolkit.wt.app.sample;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.sitoolkit.wt.infra.PropertyUtils;
import io.sitoolkit.wt.infra.SitLocaleUtils;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.infra.template.TemplateEngine;
import io.sitoolkit.wt.infra.template.TemplateModel;
import io.sitoolkit.wt.util.infra.util.FileIOUtils;

public class SampleManager {

    private String projectDir = null;

    private static final String RESOURCE_DIR = "sample/";

    private TemplateEngine templateEngine;

    public SampleManager() {
        try (AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext(
                SampleConfig.class)) {
            templateEngine = appCtx.getBean(TemplateEngine.class);
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

        generateLocalizedFile("SampleTestScript.vm", "testscript", "SampleTestScript", "csv",
                scriptProperties);
    }

    private void unarchive(String filename) {
        String resource = RESOURCE_DIR + filename;
        FileIOUtils.sysRes2file(resource, getDestPath(resource));
    }

    private Properties generateLocalizedHtml(String fileBase) {
        Properties properties = loadProperties(fileBase);
        generateLocalizedFile(fileBase + ".vm", RESOURCE_DIR, fileBase, "html", properties);

        return properties;
    }

    private void generateLocalizedFile(String template, String destDir, String destFileBase,
            String destFileExt, Properties properties) {
        TemplateModel model = new TemplateModel();
        model.setTemplate(RESOURCE_DIR + template);
        model.setOutDir(getDestPath(destDir).toString());
        model.setFileBase(destFileBase);
        model.setFileExt(destFileExt);
        model.setProperties(properties);

        templateEngine.write(model);
    }

    private Properties loadProperties(String fileBase) {
        String resourcePath = "/" + RESOURCE_DIR + getPropertiesFileName(fileBase);
        return PropertyUtils.load(resourcePath, false);
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
        new SampleManager().unarchiveBasicSample();
    }
}
