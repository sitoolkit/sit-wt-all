package org.sitoolkit.wt.infra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.sitoolkit.wt.infra.resource.MessageManager;

public class PropertyUtils {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(PropertyUtils.class);

    private static final Map<String, Properties> CACHE = new HashMap<>();

    public static Properties load(String resourceName, boolean ignoreResourceNotFound) {
        Properties prop = new Properties();
        URL url = null;

        if (resourceName.endsWith(".xml") || resourceName.endsWith(".properties")) {
            url = PropertyUtils.class.getResource(resourceName);
        } else {
            url = PropertyUtils.class.getResource(resourceName + ".properties");

            if (url == null) {
                url = PropertyUtils.class.getResource(resourceName + ".xml");
            }
        }

        if (url == null) {
            if (ignoreResourceNotFound) {
                return prop;
            } else {
                throw new ConfigurationException(
                        MessageManager.getMessage("property.not.found") + resourceName);
            }
        }

        LOG.info("property.load", url);

        try {
            if (url.getFile().endsWith("properties")) {
                prop.load(url.openStream());
            } else {
                prop.loadFromXML(url.openStream());
            }
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }

        return prop;
    }

    public static Map<String, String> loadAsMap(String resourceName,
            boolean ignoreResourceNotFound) {
        Properties prop = load(resourceName, ignoreResourceNotFound);

        Map<String, String> map = new HashMap<>();

        for (Entry<Object, Object> entry : prop.entrySet()) {
            map.put(entry.getKey().toString().trim(), entry.getValue().toString().trim());
        }

        return map;

    }

    public static void save(Object obj, File path) {
        try (FileWriter writer = new FileWriter(path)) {

            Map<String, String> map = new TreeMap<>();
            map.putAll(BeanUtils.describe(obj));

            Properties prop = new Properties();

            for (Entry<String, String> entry : map.entrySet()) {
                prop.setProperty(entry.getKey(), StringUtils.defaultString(entry.getValue()));
            }

            prop.store(writer, "");

        } catch (Exception e) {
            throw new ConfigurationException(MessageManager.getMessage("property.save.exception"),
                    e);
        }

    }

    public static Properties loadFromPathWithCache(String path) {
        Properties prop = CACHE.get(path);

        if (prop == null) {
            prop = new Properties();
        }

        try (FileInputStream fis = new FileInputStream(path)) {

            prop.load(fis);
            CACHE.put(path, prop);

            return prop;
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }

    }
}
