package io.sitoolkit.wt.gui.infra.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import io.sitoolkit.wt.util.infra.UnExpectedException;

public class ResourceUtils {

    private ResourceUtils() {
    }

    public static void copy(String resourceName, File target) {
        copy(resourceName, target.toPath());
    }

    public static void copy(String resourceName, Path target) {

        File parentDir = target.getParent().toFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            URL pomUrl = ClassLoader.getSystemResource(resourceName);
            Files.copy(pomUrl.openStream(), target);
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }
    }
}
