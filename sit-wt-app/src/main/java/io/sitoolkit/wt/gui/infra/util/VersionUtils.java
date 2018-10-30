package io.sitoolkit.wt.gui.infra.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.util.StrUtils;

public class VersionUtils {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(VersionUtils.class);

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
            LOG.warn("app.getVersionFailed", e);
        }
        return "";
    }

    public static boolean isNewer(String currentVer, String newVer) {
        if (StrUtils.isEmpty(currentVer) || StrUtils.isEmpty(newVer)) {
            return false;
        }
        return currentVer.compareTo(newVer) < 0;
    }

}
