package org.sitoolkit.wt.app.selenium2script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SeleniumStepConverterImpl implements SeleniumStepConverter, ApplicationContextAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationContext appCtx;

    private Map<String, String> seleniumIdeCommandMap;

    @Override
    public List<TestStep> convertTestScript(SeleniumTestScript seleniumTestScript, String caseNo) {
        List<TestStep> testStepList = new ArrayList<TestStep>();

        int stepNo = 1;

        for (SeleniumTestStep seleniumStep : seleniumTestScript.getTestStepList()) {
            TestStep sitStep = new TestStep();

            sitStep.setCurrentCaseNo(caseNo);
            sitStep.setNo(Integer.toString(stepNo++));

            // 操作
            String operationName = seleniumIdeCommandMap.get(seleniumStep.getCommand());
            if (operationName == null) {
                if (appCtx.containsBeanDefinition(seleniumStep.getCommand() + "Operation")) {
                    sitStep.setOperationName(seleniumStep.getCommand());
                } else {
                    log.info("Seleniumコマンド：{}は非対応です。テストスクリプトの操作は空白で出力します。",
                            seleniumStep.getCommand());
                }
            } else {
                sitStep.setOperationName(operationName);
            }

            // ロケーター
            Locator locator = Locator.build(seleniumStep.getTarget());
            sitStep.setLocator(locator);

            // テストデータ
            setTestData(sitStep, caseNo, seleniumStep.getValue());
            testStepList.add(sitStep);

            if ("open".equals(operationName)) {
                String locatorValue = sitStep.getLocator().getValue();
                sitStep.getLocator().setValue(seleniumTestScript.getBaseUrl() + locatorValue);
            }

            // スクリーンショット
            if (StringUtils.endsWith(seleniumStep.getCommand(), "AndWait")) {
                sitStep.setScreenshotTiming("前");
            }

        }
        return testStepList;
    }

    /**
     * TestStepのテストデータを設定します。
     *
     * @param testStep
     *            TestStep
     * @param caseNo
     *            ケース番号
     * @param value
     *            テストデータ
     */
    protected void setTestData(TestStep testStep, String caseNo, String value) {
        Map<String, String> testData = new HashMap<String, String>();
        String[] pair = StringUtils.split(value, "=");
        if (pair.length == 2) {
            testStep.setDataType(pair[0]);
            testData.put(caseNo, pair[1]);
        } else {
            if (StringUtils.isBlank(value)) {
                testData.put(caseNo, "y");
            } else {
                testData.put(caseNo, value);
            }
        }
        testStep.setTestData(testData);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }

    public Map<String, String> getSeleniumIdeCommandMap() {
        return seleniumIdeCommandMap;
    }

    public void setSeleniumIdeCommandMap(Map<String, String> seleniumIdeCommandMap) {
        this.seleniumIdeCommandMap = seleniumIdeCommandMap;
    }

}
