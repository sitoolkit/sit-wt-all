package org.sitoolkit.wt.gui.infra.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogUtils {

    private static final Logger LOG = Logger.getLogger(LogUtils.class.getName());

    public static void init() throws IOException {
        URL configFile = ClassLoader.getSystemResource("logging.properties");
        try (InputStream is = configFile.openStream()) {
            LogManager.getLogManager().readConfiguration(is);
            LOG.info("log configured with " + configFile);
        }
    }

    public static Logger get(Class<?> type) {
        return Logger.getLogger(type.getName());
    }
}
