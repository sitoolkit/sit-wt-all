/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.app.selenium2script;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.util.tabledata.TableDataDao;
import org.sitoolkit.wt.app.config.ExtConfig;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.TestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Selenium IDEのテストスクリプト(html)をSIT-WTのテストスクリプト(xlsx)に変換するクラスです。
 *
 * @author yuichi.kuwahara
 */
public class Selenium2Script implements ApplicationContextAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationContext appCtx;

    private TableDataDao tdDao;

    private TestScriptDao dao;

    private Map<String, String> seleniumIdeCommandMap;

    private String seleniumScriptDir = "seleniumscript";

    private String testScriptDir = "testscript";

    private String caseNo = "001";

    public Selenium2Script() {
    }

    public static void main(String[] args) {
        Selenium2Script converter = initInstance();
        System.exit(converter.execute());
    }

    public static Selenium2Script initInstance() {
        ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                Selenium2ScriptConfig.class, ExtConfig.class);
        return appCtx.getBean(Selenium2Script.class);
    }

    /**
     * Selenium IDEのテストスクリプト(html)をSIT-WTのテストスクリプト(xlsx)に変換します。
     *
     * @return 0:正常終了
     */
    public int execute() {
        File scriptDir = new File(seleniumScriptDir);
        if (!scriptDir.exists()) {
            scriptDir.mkdirs();
        }

        for (File scriptFile : FileUtils.listFiles(scriptDir, new String[] { "html" }, true)) {
            convert(scriptFile);
        }

        return 0;
    }

    public File convert(File seleniumScript) {
        // htmlの読み込み
        SeleniumTestScript list = loadSeleniumScript(seleniumScript);

        // SeleniumTestScriptオブジェクトをTestScriptオブジェクトに変換
        List<TestStep> testStepList = convertTestScript(list, caseNo);

        String sitScriptName = seleniumScript.getName().replace(".html", ".xlsx");
        File sitScriptFile = new File(testScriptDir, sitScriptName);

        dao.write(sitScriptFile, testStepList);

        return sitScriptFile;
    }

    /**
     * SeleniumScriptを読み込みます。
     *
     * @param file SeleniumScriptのファイル
     * @return SeleniumTestStep
     */
    protected SeleniumTestScript loadSeleniumScript(File file) {
        Document doc = parse(file);

        SeleniumTestScript script = new SeleniumTestScript();

        script.setBaseUrl(getBaseUrl(doc));

        NodeList tdList = doc.getElementsByTagName("td");

        List<SeleniumTestStep> list = script.getTestStepList();

        SeleniumTestStep testStep = appCtx.getBean(SeleniumTestStep.class);

        for (int i = 1; i < tdList.getLength(); i++) {
            String nodeValue = tdList.item(i).getTextContent();

            switch ((i - 1) % 3) {
            case 0:
                testStep.setCommand(nodeValue);
                break;
            case 1:
                testStep.setTarget(nodeValue);
                break;
            case 2:
                testStep.setValue(nodeValue);
                list.add(testStep);
                log.debug("Seleniumテストスクリプトを1行読み込みました　command:{},target:{},value:{}",
                        testStep.getCommand(), testStep.getTarget(), testStep.getValue());
                testStep = new SeleniumTestStep();
                break;
            default:
                break;
            }
        }

        return script;
    }

    /**
     * XMLファイルを読み込んでDOMにパースします。
     *
     * @param file
     *            XMLファイル
     * @return DOM
     */
    private Document parse(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId)
                        throws SAXException, IOException {
                    return new InputSource(new StringReader(""));
                }
            });

            return builder.parse(file);
        } catch (Exception e) {
            throw new TestException(e);
        }
    }

    private String getBaseUrl(Document doc) {
        NodeList linkNodes = doc.getElementsByTagName("link");

        for (int i = 0; i < linkNodes.getLength(); i++) {
            Element linkNode = (Element) linkNodes.item(i);

            if ("selenium.base".equals(linkNode.getAttribute("rel"))) {
                return linkNode.getAttribute("href");
            }
        }
        return "";
    }

    /**
     * SeleniumTestStepをSIToolkitのTestStepに変換します。
     *
     * @param seleniumTestScript
     *            SeleniumTestScript
     * @param caseNo
     *            ケース番号
     * @return
     */
    protected List<TestStep> convertTestScript(SeleniumTestScript seleniumTestScript,
            String caseNo) {

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

    public TestScriptDao getDao() {
        return dao;
    }

    public void setDao(TestScriptDao dao) {
        this.dao = dao;
    }

    public String getSeleniumScriptDir() {
        return seleniumScriptDir;
    }

    public void setSeleniumScriptDir(String seleniumScriptDir) {
        this.seleniumScriptDir = seleniumScriptDir;
    }

    public String getTestScriptDir() {
        return testScriptDir;
    }

    public void setTestScriptDir(String testScriptDir) {
        this.testScriptDir = testScriptDir;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public TableDataDao getTdDao() {
        return tdDao;
    }

    public void setTdDao(TableDataDao tdDao) {
        this.tdDao = tdDao;
    }

}
