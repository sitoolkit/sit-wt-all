package org.sitoolkit.wt.gui.infra.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sitoolkit.wt.gui.infra.log.LogUtils;

public class VersionUtils {

    private static final Logger LOG = LogUtils.get(VersionUtils.class);

    private VersionUtils() {
    }

    public static String get() {
        try {
            Enumeration<URL> resources = VersionUtils.class.getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");

            while (resources.hasMoreElements()) {
                URL res = resources.nextElement();

                Manifest manifest = new Manifest(res.openStream());

                if ("sit-wt-app"
                        .equals(manifest.getMainAttributes().getValue("Implementation-Title"))) {
                    return manifest.getMainAttributes().getValue("Implementation-Version");
                }
            }

        } catch (IOException e) {
            LOG.log(Level.WARNING, "fail to get version", e);
        }
        return "";
    }
}
