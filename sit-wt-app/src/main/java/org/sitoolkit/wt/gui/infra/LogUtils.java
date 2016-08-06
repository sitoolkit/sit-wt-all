package org.sitoolkit.wt.gui.infra;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogUtils {

    private static final Logger LOG = Logger.getLogger(LogUtils.class.getName());

    public static void init() {
        URL configFile = ClassLoader.getSystemResource("logging.properties");
        try (InputStream is = configFile.openStream()) {
            LogManager.getLogManager().readConfiguration(is);
            LOG.info("log configured with " + configFile);
        } catch (IOException e) {
            // TODO 例外処理
            e.printStackTrace();
        }
    }
}
