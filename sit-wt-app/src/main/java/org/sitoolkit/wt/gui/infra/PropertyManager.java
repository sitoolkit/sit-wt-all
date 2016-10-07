package org.sitoolkit.wt.gui.infra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyManager {

    private static final String SIT_WT_CLASSPATH = "sitWtClasspath";

    private static final String FILE_NAME = "sit-wt-app.properties";

    private static final String SEPARATOR = ",";

    private static final String BASE_URL = "baseUrl";

    private static final String BASE_URL_LIMIT = "baseUrlLimit";

    private static final String SELECTED_BASE_URL = "selectedBaseUrl";

    private static final Logger LOG = Logger.getLogger(PropertyManager.class.getName());

    private static final PropertyManager pm = new PropertyManager();

    private Properties prop = new Properties();

    private File baseDir;

    private List<String> baseUrls = new ArrayList<>();

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
            LOG.log(Level.INFO, "loaded properties : {0}", prop);

            List<String> savedBaseUrls = Arrays.asList(getProp(BASE_URL).split(SEPARATOR));
            savedBaseUrls = savedBaseUrls.subList(0,
                    Math.min(savedBaseUrls.size(), getBaseUrlLimit()));
            baseUrls.addAll(savedBaseUrls);

        } catch (IOException e) {

            LOG.log(Level.WARNING, "exception in loading properties", e);

        }
    }

    public void save() {

        if (baseDir == null) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(new File(baseDir, FILE_NAME), false)) {

            setProp(BASE_URL, StrUtils.join(baseUrls));

            prop.store(fos, "SI-Toolkit for Web Testing");
            LOG.log(Level.INFO, "saved properties : {0}", prop);

        } catch (IOException e) {

            LOG.log(Level.WARNING, "exception in saving properties", e);

        }

    }

    public void setBaseUrls(List<String> baseUrls) {
        this.baseUrls = baseUrls;
    }

    public List<String> getBaseUrls() {
        return baseUrls;
    }

    public int getBaseUrlLimit() {
        return Integer.parseInt(getProp(BASE_URL_LIMIT, "5"));
    }

    public void setClasspath(String classpath) {
        setProp(SIT_WT_CLASSPATH, classpath);
    }

    public String getClasspath() {
        return getProp(SIT_WT_CLASSPATH);
    }

    private String getProp(String key) {
        return getProp(key, "");
    }

    private String getProp(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    private void setProp(String key, String value) {
        prop.setProperty(key, value);
    }

}
