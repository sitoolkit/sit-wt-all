package io.sitoolkit.wt.app.selenium2script;

import java.util.ArrayList;
import java.util.List;

public class SeleniumTestScript {

    private List<SeleniumTestStep> testStepList = new ArrayList<>();

    private String baseUrl;

    public List<SeleniumTestStep> getTestStepList() {
        return testStepList;
    }

    public void setTestStepList(List<SeleniumTestStep> testStepList) {
        this.testStepList = testStepList;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
