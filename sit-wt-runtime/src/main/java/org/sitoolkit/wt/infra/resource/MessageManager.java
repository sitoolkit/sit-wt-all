package org.sitoolkit.wt.infra.resource;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public class MessageManager {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(MessageManager.class);

    private static ResourceBundle resource;

    private static ResourceBundle getResource() {
        if (resource == null) {
            String baseName = MessageManager.class.getPackage().getName().replace(".", "/")
                    + "/message";
            resource = ResourceBundle.getBundle(baseName);
        }
        return resource;
    }

    public static String getMessage(String key) {
        try {
            return getResource().getString(key);
        } catch (MissingResourceException e) {
            LOG.warn("warn2", e.getMessage());
        }
        return "!! messing resource !!";

    }

    public static String getMessage(String key, Object... params) {
        try {
            return MessageFormatter.arrayFormat(getResource().getString(key), params).getMessage();
        } catch (MissingResourceException e) {
            LOG.warn("warn2", e.getMessage());
        }
        return "!! messing resource !!";

    }

}
