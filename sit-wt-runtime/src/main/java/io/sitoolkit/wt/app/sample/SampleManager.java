package io.sitoolkit.wt.app.sample;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.infra.SitLocaleUtils;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.infra.resource.SitResourceUtils;

public class SampleManager {

    private String projectDir = null;

    private static final String RESOURCE_DIR = "sample/";

    public void unarchiveBasicSample(String projectDir) {
        this.projectDir = projectDir;
        unarchiveBasicSample();
    }

    public void unarchiveBasicSample() {
        unarchive("bootstrap.min.css");
        unarchive("pom.xml");

        Properties inputProperties = createLocalizedFile("input.html");
        Properties termsProperties = createLocalizedFile("terms.html");
        Properties doneProperties = createLocalizedFile("done.html");

        Properties scriptProperties = loadProperties("CsvTestScript");
        scriptProperties.putAll(inputProperties);
        scriptProperties.putAll(termsProperties);
        scriptProperties.putAll(doneProperties);
        scriptProperties.putAll(MessageManager.getMessageMap("testScript-"));

        Path scriptTemplate = getDestPath("testscript/SampleTestScript.csv");
        createLocalizedFile(SitResourceUtils.res2str(RESOURCE_DIR + "CsvTestScript.csv"),
                scriptTemplate, scriptProperties);
    }

    private void unarchive(String resource) {
        String path = RESOURCE_DIR + resource;
        SitResourceUtils.res2file(path, getDestPath(path));
    }

    private Properties createLocalizedFile(String filename) {
        String name = FilenameUtils.removeExtension(filename);
        String template = RESOURCE_DIR + filename;
        Path dest = getDestPath(template);

        Properties properties = loadProperties(name);
        createLocalizedFile(SitResourceUtils.res2str(template), dest, properties);
        return properties;
    }

    private void createLocalizedFile(String template, Path dest, Properties properties) {
        VelocityContext context = new VelocityContext(properties);

        try (FileOutputStream fos = new FileOutputStream(dest.toFile());
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                BufferedWriter bw = new BufferedWriter(osw);) {

            Velocity.evaluate(context, bw, template, template);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties loadProperties(String name) {
        try {
            URL url = ResourceUtils
                    .getURL("classpath:" + RESOURCE_DIR + getPropertiesFileName(name));
            Properties properties = new Properties();
            properties.load(url.openStream());
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
