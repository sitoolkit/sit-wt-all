package io.sitoolkit.wt.app.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.infra.ConfigurationException;

public class SampleManager {

    private static final String CHARSET = "UTF-8";

    private String projectDir = null;

    public void unarchiveBasicSample() {
        unarchive("sample/bootstrap.min.css");
        unarchive("sample/pom.xml");

        Properties inputProperties = createLocalizeFile("input.html");
        Properties termsProperties = createLocalizeFile("terms.html");
        Properties doneProperties = createLocalizeFile("done.html");

        Properties scriptProperties = loadProperties("CsvTestScript");
        scriptProperties.putAll(inputProperties);
        scriptProperties.putAll(termsProperties);
        scriptProperties.putAll(doneProperties);

        File scriptTemplateDest = new File(getDestDir("testscript"), "SampleTestScript.csv");
        applyProperties(resource2str("sample/CsvTestScript.csv"), scriptTemplateDest.toString(),
                scriptProperties);
    }

    private Properties createLocalizeFile(String filename) {
        String name = FilenameUtils.removeExtension(filename);
        String templateResource = "sample/" + filename;
        String dest = getDestDir("") + templateResource;

        Properties properties = loadProperties(name);
        applyProperties(resource2str(templateResource), dest, properties);
        return properties;
    }

    private void applyProperties(String template, String dest, Properties properties) {
        VelocityContext context = new VelocityContext(properties);

        try (FileOutputStream fos = new FileOutputStream(new File(dest));
                OutputStreamWriter osw = new OutputStreamWriter(fos, CHARSET);
                BufferedWriter bw = new BufferedWriter(osw);) {

            new VelocityEngine().evaluate(context, bw, "name", template);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void unarchive(String resource) {
        File destFile = new File(getDestDir(resource));
        try {
            URL url = ResourceUtils.getURL("classpath:" + resource);
            FileUtils.copyInputStreamToFile(url.openStream(), destFile);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    private String resource2str(String resource) {
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource);
            return IOUtils.toString(stream, Charset.defaultCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
