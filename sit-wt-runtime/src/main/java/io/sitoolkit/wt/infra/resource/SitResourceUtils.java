package io.sitoolkit.wt.infra.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ResourceUtils;

import io.sitoolkit.wt.infra.ConfigurationException;

public class SitResourceUtils {

    public static String res2str(String resource) {
        try {
            InputStream stream = SitResourceUtils.class.getClassLoader()
                    .getResourceAsStream(resource);
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void res2file(String resource, Path dest) {
        try {
            URL url = ResourceUtils.getURL("classpath:" + resource);
            FileUtils.copyInputStreamToFile(url.openStream(), dest.toFile());
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }

}
