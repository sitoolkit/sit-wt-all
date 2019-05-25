/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.sitoolkit.wt.app.selenium2script;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sitoolkit.wt.app.config.ExtConfig;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.util.StrUtils;

/**
 * Selenium IDEのテストスクリプト(html)をSIT-WTのテストスクリプト(csv)に変換するクラスです。
 *
 * @author yuichi.kuwahara
 */
public class Selenium2Script {

  protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

  private TestScriptDao dao;

  private SeleniumStepConverter seleniumStepConverter;

  private String outputDir = "testscript";

  private String seleniumScriptDirs = outputDir + ",.";

  private String caseNo = "001";

  private boolean openScript = true;

  private boolean overwriteScript = false;

  public Selenium2Script() {}

  public static void main(String[] args) {
    Selenium2Script converter = initInstance();
    System.exit(converter.execute());
  }

  public static Selenium2Script initInstance() {
    ApplicationContext appCtx =
        new AnnotationConfigApplicationContext(Selenium2ScriptConfig.class, ExtConfig.class);
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
      for (File seleniumScript : FileUtils.listFiles(scriptDir, new String[] {"side"}, recursive)) {
        List<Path> scripts = convert(seleniumScript);

        backup(seleniumScript);

        if (!isOpenScript()) {
          continue;
        }

        for (Path script : scripts) {
          try {
            Desktop.getDesktop().open(script.toFile());
          } catch (IOException e) {
            log.error("open.script.error", e);
            ret = 2;
          }
        }
      }
    }

    return ret;
  }

  public List<Path> convert(File seleniumScript) {
    log.info("selenium.script.convert", seleniumScript.getAbsolutePath());

    List<SeleniumTestScript> scripts = loadSeleniumScripts(seleniumScript);

    String baseName = FilenameUtils.getBaseName(seleniumScript.getName());

    return scripts.stream().map((script) -> {
      List<TestStep> testStepList = seleniumStepConverter.convertTestScript(script, caseNo);
      return write(baseName, script.getName(), testStepList);
    }).collect(Collectors.toList());
  }

  private Path write(String baseName, String caseName, List<TestStep> testStepList) {
    Path dest =
        Paths.get(outputDir, baseName + "_" + StrUtils.sanitizeMetaCharacter(caseName) + ".csv");
    dao.write(dest.toFile(), testStepList, overwriteScript);

    return dest;
  }

  public void backup(File seleniumScript) {
    File bkFile = new File(seleniumScript.getParentFile(), seleniumScript.getName() + ".bk");

    log.info("selenium.script.backup", seleniumScript.getAbsolutePath(), bkFile);

    seleniumScript.renameTo(bkFile);
  }

  /**
   * SeleniumScriptを読み込みます。
   *
   * @param file SeleniumScriptのファイル
   * @return SeleniumTestStep
   */
  protected List<SeleniumTestScript> loadSeleniumScripts(File file) {
    ObjectMapper mapper = new ObjectMapper();
    List<SeleniumTestScript> scripts = new ArrayList<>();

    try {
      JsonNode node = mapper.readTree(file);
      String baseUrl = node.get("url").asText();

      for (JsonNode testNode : node.get("tests")) {
        scripts.add(loadSeleniumScript(testNode, baseUrl));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return scripts;

  }

  protected SeleniumTestScript loadSeleniumScript(JsonNode testNode, String baseUrl) {
    SeleniumTestScript script = new SeleniumTestScript();
    script.setBaseUrl(baseUrl);
    script.setName(testNode.get("name").asText());

    for (JsonNode commandNode : testNode.get("commands")) {

      SeleniumTestStep testStep = new SeleniumTestStep();

      testStep.setCommand(commandNode.get("command").asText());
      testStep.setTarget(commandNode.get("target").asText());
      testStep.setValue(commandNode.get("value").asText());

      script.getTestStepList().add(testStep);
      System.out.println("TestStep: " + testStep.getCommand() + " : " + testStep.getTarget() + " : "
          + testStep.getValue());
      log.debug("test.step.load", testStep.getCommand(), testStep.getTarget(), testStep.getValue());
    }

    return script;
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
