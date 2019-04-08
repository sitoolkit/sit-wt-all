package io.sitoolkit.wt.infra;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

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

}
