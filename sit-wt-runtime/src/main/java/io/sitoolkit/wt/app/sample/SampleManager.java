package io.sitoolkit.wt.app.sample;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.infra.ConfigurationException;

public class SampleManager {

    private static final String DEFAULT_INPUT_LANGUAGE_FILE = "input_language.js";

    private String projectDir = null;

    public SampleManager() {
    }

    public void unarchive(String resource, File dest) {
        URL res = ClassLoader.getSystemResource(resource);

        try {
            res = ResourceUtils.getURL("classpath:" + resource);
            FileUtils.copyInputStreamToFile(res.openStream(), dest);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

    public void unarchiveBasicSample() {
        unarchive("sample/input.html");
        unarchive(getInputLanguageFilePath(), DEFAULT_INPUT_LANGUAGE_FILE);
        unarchive("sample/vue.min.js");
        unarchive("sample/done.html");
        unarchive("sample/terms.html");
        unarchive("sample/bootstrap.min.css");
        unarchive("sample/pom.xml");
        unarchive("sample/CsvTestScript.csv",
                new File(getDestDir("testscript"), "SampleTestScript.csv"));
    }

    private void unarchive(String resource) {
        String[] dest = resource.split("/");
        unarchive(resource, new File(getDestDir(dest[0]), dest[1]));
    }

    private void unarchive(String resource, String destFile) {
        String[] dest = resource.split("/");
        unarchive(resource, new File(getDestDir(dest[0]), destFile));
    }

    private String getInputLanguageFilePath() {
        if (Locale.getDefault().equals(Locale.JAPAN)) {
            return "sample/input_language_" + Locale.JAPAN.getLanguage() + ".js";
        } else {
            return "sample/" + DEFAULT_INPUT_LANGUAGE_FILE;
        }
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
