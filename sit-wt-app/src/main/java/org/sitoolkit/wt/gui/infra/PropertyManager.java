package org.sitoolkit.wt.gui.infra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyManager {

    private static final String FILE_NAME = "sit-wt-app.properties";

    private static final String SEPARATOR = ",";

    private static final String BASE_URL = "baseUrl";

    private static final String SELECTED_BASE_URL = "selectedBaseUrl";

    private static final Logger LOG = Logger.getLogger(PropertyManager.class.getName());

    private static final PropertyManager pm = new PropertyManager();

    private Properties prop = new Properties();

    private File baseDir;

    private PropertyManager() {
    }

    public static PropertyManager get() {
        return pm;
    }

    public void load(File baseDir) {

        this.baseDir = baseDir;

        File propertyFile = new File(baseDir, FILE_NAME);

        if (!propertyFile.exists()) {
            LOG.log(Level.CONFIG, "no property file ", baseDir.getAbsolutePath());
            return;
        }

        try (FileInputStream fis = new FileInputStream(propertyFile)) {

            prop.load(fis);
            LOG.log(Level.INFO, "loaded properties : ", prop);

        } catch (IOException e) {

            LOG.log(Level.WARNING, "exception in loading properties", e);

        }
    }

    public void save() {

        if (baseDir == null) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(new File(baseDir, FILE_NAME), false)) {

            prop.store(fos, "");

            LOG.log(Level.INFO, "saved properties : ", prop);

        } catch (IOException e) {

            LOG.log(Level.WARNING, "exception in saving properties", e);

        }

    }

    public List<String> getBaseUrls() {

        List<String> baseUrls = new ArrayList<>();

        for (String baseUrl : prop.getProperty(BASE_URL).split(SEPARATOR)) {
            baseUrls.add(baseUrl.trim());
        }

        return baseUrls;

    }

    public void addBaseUrl(String baseUrl) {

        String existingBaseUrls = prop(BASE_URL);
        if (!existingBaseUrls.isEmpty()) {
            baseUrl = baseUrl + SEPARATOR;
        }

        prop(BASE_URL, baseUrl + prop(BASE_URL));
    }

    public String getSelectedBaseUrl() {
        return prop(SELECTED_BASE_URL);
    }

    public void setSelectedBaseUrl(String selectedBaseUrl) {
        prop(SELECTED_BASE_URL, selectedBaseUrl);
    }

    private String prop(String key) {
        return prop.getProperty(key, "");
    }

    private void prop(String key, String value) {
        prop.setProperty(key, value);
    }
}
