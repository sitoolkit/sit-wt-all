package io.sitoolkit.wt.infra.resource;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.infra.ConfigurationException;

public class SitResourceUtils {

    public static void res2file(String resource, Path dest) {
        try {
            URL url = ResourceUtils.getURL("classpath:" + resource);
            FileUtils.copyInputStreamToFile(url.openStream(), dest.toFile());
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

}
