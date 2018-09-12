package io.sitoolkit.wt.gui.domain.test;

import java.io.File;

public class TestRunParams {

    private File baseDir;

    private String baseUrl;

    private boolean debug;

    private boolean parallel;

    private boolean compareScreenshot;

    private String driverType;

    private String targetScripts;

    public TestRunParams() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public boolean isCompareScreenshot() {
        return compareScreenshot;
    }

    public void setCompareScreenshot(boolean compareScreenshot) {
        this.compareScreenshot = compareScreenshot;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public String getTargetScripts() {
        return targetScripts;
    }

    public void setTargetScriptsStr(String targetScripts) {
        this.targetScripts = targetScripts;
    }

}
