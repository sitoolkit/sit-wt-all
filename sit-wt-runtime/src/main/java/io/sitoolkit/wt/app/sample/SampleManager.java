package io.sitoolkit.wt.app.sample;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.infra.ConfigurationException;

public class SampleManager {

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
        unarchive("sample/done.html");
        unarchive("sample/terms.html");
        unarchive("sample/bootstrap.min.css");
        unarchive("sample/pom.xml");
        unarchive("sample/CsvTestScript.csv", new File("testscript", "SampleTestScript.csv"));
    }

    private void unarchive(String resource) {
        String[] dest = resource.split("/");
        unarchive(resource, new File(dest[0], dest[1]));
    }

    public static void main(String[] args) {
        new SampleManager().unarchiveBasicSample();
    }
}