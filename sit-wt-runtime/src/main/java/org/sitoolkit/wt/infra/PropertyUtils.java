package org.sitoolkit.wt.infra;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyUtils.class);

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
                throw new ConfigurationException("プロパティファイルが見つかりません。" + resourceName);
            }
        }

        LOG.info("プロパティを読み込みます。{}", url);

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
            map.put(entry.getKey().toString(), entry.getValue().toString());
        }

        return map;

    }
}
