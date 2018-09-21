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
package io.sitoolkit.wt.app.selenium2script;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
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

import io.sitoolkit.wt.app.config.ExtConfig;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.TestException;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

/**
 * Selenium IDEのテストスクリプト(html)をSIT-WTのテストスクリプト(csv)に変換するクラスです。
 *
 * @author yuichi.kuwahara
 */
public class Selenium2Script implements ApplicationContextAware {

    protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

    private ApplicationContext appCtx;

    private TestScriptDao dao;

    private SeleniumStepConverter seleniumStepConverter;

    private String outputDir = "testscript";

    private String seleniumScriptDirs = outputDir + ",.";

    private String caseNo = "001";

    private boolean openScript = true;

    private boolean overwriteScript = false;

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
     * Selenium IDEのテストスクリプト(html)をSIT-WTのテストスクリプト(csv)に変換します。
     *
     * @return 0:正常終了
     */
    public int execute() {

        int ret = 0;

        for (String seleniumScriptDir : seleniumScriptDirs.split(",")) {
            File scriptDir = new File(seleniumScriptDir);
            if (!scriptDir.exists()) {
                continue;
            }

            boolean recursive = !".".equals(seleniumScriptDir);
            for (File seleniumScript : FileUtils.listFiles(scriptDir, new String[] { "html" },
                    recursive)) {
                File sitScript = convert(seleniumScript);

                backup(seleniumScript);

                if (isOpenScript()) {
                    try {
                        Desktop.getDesktop().open(sitScript);
                    } catch (IOException e) {
                        log.error("open.script.error", e);
                        ret = 2;
                    }
                }
            }
        }

        return ret;
    }

    public File convert(File seleniumScript) {
        log.info("selenium.script.convert", seleniumScript.getAbsolutePath());

        // htmlの読み込み
        SeleniumTestScript list = loadSeleniumScript(seleniumScript);

        // SeleniumTestScriptオブジェクトをTestScriptオブジェクトに変換
        List<TestStep> testStepList = seleniumStepConverter.convertTestScript(list, caseNo);

        String sitScriptName = seleniumScript.getName().replace(".html", ".csv");
        File sitScriptFile = new File(outputDir, sitScriptName);

        dao.write(sitScriptFile, testStepList, overwriteScript);

        return sitScriptFile;
    }

    public void backup(File seleniumScript) {
        File bkFile = new File(seleniumScript.getParentFile(), seleniumScript.getName() + ".bk");

        log.info("selenium.script.backup", seleniumScript.getAbsolutePath(), bkFile);

        seleniumScript.renameTo(bkFile);
    }

    /**
     * SeleniumScriptを読み込みます。
     *
     * @param file
     *            SeleniumScriptのファイル
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
                    log.debug("test.step.load", testStep.getCommand(), testStep.getTarget(),
                            testStep.getValue());
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }

    public TestScriptDao getDao() {
        return dao;
    }

    public void setDao(TestScriptDao dao) {
        this.dao = dao;
    }

    public String getSeleniumScriptDir() {
        return seleniumScriptDirs;
    }

    public void setSeleniumScriptDir(String seleniumScriptDir) {
        this.seleniumScriptDirs = seleniumScriptDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setSeleniumStepConverter(SeleniumStepConverter seleniumStepConverter) {
        this.seleniumStepConverter = seleniumStepConverter;
    }

    public boolean isOpenScript() {
        return openScript;
    }

    public void setOpenScript(boolean openScript) {
        this.openScript = openScript;
    }

    public boolean isOverwriteScript() {
        return overwriteScript;
    }

    public void setOverwriteScript(boolean overwriteScript) {
        this.overwriteScript = overwriteScript;
    }

}
