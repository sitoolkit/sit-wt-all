package org.sitoolkit.wt.gui.infra.log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogUtils {

    private static final Logger LOG = Logger.getLogger(LogUtils.class.getName());

    static {
        init();
    }

    public static void init() {
        File logDir = new File("log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        URL configFile = ClassLoader.getSystemResource("logging.properties");
        try (InputStream is = configFile.openStream()) {
            LogManager.getLogManager().readConfiguration(is);
            LOG.info("log configured with " + configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger get(Class<?> type) {
        return Logger.getLogger(type.getName());
    }
}
