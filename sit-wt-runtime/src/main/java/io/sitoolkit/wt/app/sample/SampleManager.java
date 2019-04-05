package io.sitoolkit.wt.app.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.infra.ConfigurationException;

public class SampleManager {

    private static final String CHARSET = "UTF-8";

    private String projectDir = null;

    public SampleManager() {
    }

    private void unarchive(String resource, File dest) {
        URL res = ClassLoader.getSystemResource(resource);

        try {
            res = ResourceUtils.getURL("classpath:" + resource);
            FileUtils.copyInputStreamToFile(res.openStream(), dest);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    public void unarchiveBasicSample() {
        unarchive("sample/bootstrap.min.css");
        unarchive("sample/pom.xml");

        Properties inputProperties = unarchiveAndApplyProperties("input.html");
        Properties termsProperties = unarchiveAndApplyProperties("terms.html");
        Properties doneProperties = unarchiveAndApplyProperties("done.html");

        Properties scriptProperties = loadProperties("CsvTestScript");
        scriptProperties.putAll(inputProperties);
        scriptProperties.putAll(termsProperties);
        scriptProperties.putAll(doneProperties);

        File scriptTemplateDest = new File(getDestDir("testscript"), "SampleTestScript.csv");
        unarchive("sample/CsvTestScript_template.csv", scriptTemplateDest);

        applyProperties(scriptTemplateDest.toPath(), scriptTemplateDest.toString(),
                scriptProperties);
    }

    private Properties unarchiveAndApplyProperties(String filename) {
        String name = FilenameUtils.removeExtension(filename);
        String extension = FilenameUtils.getExtension(filename);
        String templateResource = "sample/" + name + "_template." + extension;
        // String templateDest = getDestDir(".") + "/" + templateResource;
        String dest = getDestDir(".") + "/sample/" + filename;

        Path templatePath = unarchive(templateResource);
        Properties properties = loadProperties(name);
        return applyProperties(templatePath, dest, properties);
    }

    private Properties applyProperties(Path templatePath, String dest, Properties properties) {
        VelocityContext context = new VelocityContext(properties);

        try (FileOutputStream fos = new FileOutputStream(new File(dest));
                OutputStreamWriter osw = new OutputStreamWriter(fos, CHARSET);
                BufferedWriter bw = new BufferedWriter(osw);) {

            VelocityEngine velocity = new VelocityEngine();
            Template velocityTemplate = velocity.getTemplate(templatePath.toString(), CHARSET);
            velocityTemplate.merge(context, bw);

            // Velocity.evaluate(context, bw, "name", "${input-page-title}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    private Path unarchive(String resource) {
        String[] dest = resource.split("/");
        File destFile = new File(getDestDir(dest[0]), dest[1]);
        unarchive(resource, destFile);
        return destFile.toPath();
    }

    private Properties loadProperties(String name) {
        try {
            URL url = ResourceUtils.getURL("classpath:sample/" + getPropertiesFileName(name));
            Properties properties = new Properties();
            properties.load(url.openStream());
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPropertiesFileName(String name) {
        String fileName;
        if (Locale.getDefault().equals(Locale.JAPAN)) {
            fileName = name + "_" + Locale.JAPAN.getLanguage();
        } else {
            fileName = name;
        }
        return fileName + ".properties";
    }

    public void unarchiveBasicSample(String projectDir) {
        this.projectDir = projectDir;
        unarchiveBasicSample();
    }

    public String getDestDir(String dir) {
        return (projectDir == null) ? dir : projectDir + "/" + dir;
    }

    public static void main(String[] args) {
        new SampleManager().unarchiveBasicSample();
    }
}
