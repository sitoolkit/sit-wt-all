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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.sitoolkit.wt.app.config.ExtConfig;
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

    private TestScriptDao dao;

    private SeleniumStepConverter seleniumStepConverter;

    private String seleniumScriptDirs = "seleniumscript,.";

    private String outputDir = "testscript";

    private String backupDir = "seleniumscript-bk";

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
     * Selenium IDEのテストスクリプト(html)をSIT-WTのテストスクリプト(xlsx)に変換します。
     *
     * @return 0:正常終了
     */
    public int execute() {

        int ret = 0;
        File bkdir = new File(backupDir);

        for (String seleniumScriptDir : seleniumScriptDirs.split(",")) {
            File scriptDir = new File(seleniumScriptDir);
            if (!scriptDir.exists()) {
                continue;
            }

            boolean recursive = !".".equals(seleniumScriptDir);
            for (File seleniumScript : FileUtils.listFiles(scriptDir, new String[] { "html" },
                    recursive)) {
                File sitScript = convert(seleniumScript);

                try {
                    log.info("Seleniumスクリプトを退避します {} -> {}", seleniumScript.getAbsolutePath(),
                            bkdir.getAbsolutePath());
                    FileUtils.moveFileToDirectory(seleniumScript, bkdir, true);
                } catch (IOException e) {
                    log.warn("Seleniumスクリプトの退避に失敗しました", e);
                    ret = 1;
                }

                if (isOpenScript()) {
                    try {
                        Desktop.getDesktop().open(sitScript);
                    } catch (IOException e) {
                        log.error("変換後のスクリプトを開けませんでした", e);
                        ret = 2;
                    }
                }
            }
        }

        return ret;
    }

    public File convert(File seleniumScript) {
        log.info("Seleniumスクリプトを変換します。{}", seleniumScript.getAbsolutePath());

        // htmlの読み込み
        SeleniumTestScript list = loadSeleniumScript(seleniumScript);

        // SeleniumTestScriptオブジェクトをTestScriptオブジェクトに変換
        List<TestStep> testStepList = seleniumStepConverter.convertTestScript(list, caseNo);

        String sitScriptName = seleniumScript.getName().replace(".html", ".xlsx");
        File sitScriptFile = new File(outputDir, sitScriptName);

        dao.write(sitScriptFile, testStepList, overwriteScript);

        return sitScriptFile;
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
