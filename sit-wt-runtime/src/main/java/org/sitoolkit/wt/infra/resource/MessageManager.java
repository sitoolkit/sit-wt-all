package org.sitoolkit.wt.infra.resource;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageManager {

    private static final Logger LOG = LoggerFactory.getLogger(MessageManager.class);

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
            LOG.warn("{}", e.getMessage());
        }
        return "!! messing resource !!";

    }

}
