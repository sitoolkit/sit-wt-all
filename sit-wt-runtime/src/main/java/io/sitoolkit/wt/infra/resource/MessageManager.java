package io.sitoolkit.wt.infra.resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public class MessageManager {

    private static final Logger LOG = LoggerFactory.getLogger(MessageManager.class);

    private static ResourceBundle resource;

    private static synchronized ResourceBundle getResource() {
        if (resource == null) {
            String baseName = MessageManager.class.getPackage().getName().replace(".", "/")
                    + "/message";
            LOG.info("message resource is initialized with locale {}", Locale.getDefault());
            resource = ResourceBundle.getBundle(baseName);
        }
        return resource;
    }

    public static String getMessage(String key) {
        try {
            return getResource().getString(key);
        } catch (MissingResourceException e) {
            LOG.warn("{}, locale {}", e.getMessage(), getResource().getLocale());
        }
        return "!! messing resource !!";

    }

    public static String getMessage(String key, Object... params) {
        try {
            return MessageFormatter.arrayFormat(getResource().getString(key), params).getMessage();
        } catch (MissingResourceException e) {
            LOG.warn("{}, locale {}", e.getMessage(), getResource().getLocale());
        }
        return "!! messing resource !!";

    }

    public static Map<String, String> getMessageMap(String keyPrefix) {
        Map<String, String> messageMap = new HashMap<>();
        for (String key : Collections.list(getResource().getKeys())) {
            if (key.startsWith(keyPrefix)) {
                messageMap.put(key, getResource().getString(key));
            }
        }
        return messageMap;
    }

}
