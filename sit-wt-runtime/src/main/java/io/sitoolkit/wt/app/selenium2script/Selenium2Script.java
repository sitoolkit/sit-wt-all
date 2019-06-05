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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.sitoolkit.wt.app.config.ExtConfig;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.JsonUtils;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.util.infra.util.StrUtils;

/**
 * Selenium IDEのテストスクリプト(.side)をSIT-WTのテストスクリプト(.csv)に変換するクラスです。
 *
 * @author yuichi.kuwahara
 */
public class Selenium2Script {

  protected final SitLogger log = SitLoggerFactory.getLogger(getClass());

  public static final String DEFAULT_CASE_NO = "001";

  public static final String SCRIPT_EXTENSION = "side";

  private static final String SELENIUM_SCRIPT_DIR = "testscript";

  private TestScriptDao dao;

  private SeleniumStepConverter seleniumStepConverter;

  private String outputDir = SELENIUM_SCRIPT_DIR;

  private boolean openScript = true;

  private boolean overwriteScript = false;

  public Selenium2Script() {}

  public static void main(String[] args) {
    Selenium2Script instance = initInstance();
    instance.setOverwriteScript(true);
    instance.execute();
  }

  public static Selenium2Script initInstance() {
    try (AnnotationConfigApplicationContext appCtx =
        new AnnotationConfigApplicationContext(Selenium2ScriptConfig.class, ExtConfig.class)) {
      return appCtx.getBean(Selenium2Script.class);
    }
  }

  /**
   * Selenium IDEのテストスクリプト(.side)をSIT-WTのテストスクリプト(.csv)に変換します。
   */
  public void execute() {

    List<Path> testScripts = convertScriptFiles();

    if (isOpenScript()) {
      openTestScripts(testScripts);
    }
  }

  private List<Path> convertScriptFiles() {

    File scriptDir = new File(getProjectDirectory(), SELENIUM_SCRIPT_DIR);
    if (!scriptDir.exists()) {
      return Collections.emptyList();
    }

    getOutputDirPath().toFile().mkdirs();

    List<Path> testScripts = new ArrayList<>();
    FileUtils.listFiles(scriptDir, new String[] {SCRIPT_EXTENSION}, true)
        .forEach((seleniumScript) -> {
          testScripts.addAll(convertScriptFile(seleniumScript.toPath()));
          backup(seleniumScript.toPath());
        });

    return testScripts;
  }

  public List<Path> convertScriptFile(Path sourcePath) {
    log.info("selenium.script.convert", sourcePath.toAbsolutePath());

    List<SeleniumTestScript> seleniumScripts = loadSeleniumScripts(sourcePath);

    return seleniumScripts.stream().map((seleniumScript) -> {
      List<TestStep> testStepList =
          seleniumStepConverter.convertTestScript(seleniumScript, DEFAULT_CASE_NO);

      Path outputPath = buildOutputPath(sourcePath, seleniumScript.getName());
      dao.write(outputPath.toFile(), testStepList, overwriteScript);

      return outputPath;
    }).collect(Collectors.toList());
  }

  private Path buildOutputPath(Path sourcePath, String testName) {
    String baseName = FilenameUtils.getBaseName(sourcePath.toString());
    String fileName = baseName + "_" + StrUtils.sanitizeMetaCharacter(testName) + ".csv";
    return getOutputDirPath().resolve(fileName);
  }

  public void backup(Path seleniumScript) {
    Path bkFile = seleniumScript.resolveSibling(seleniumScript.getFileName() + ".bk");

    log.info("selenium.script.backup", seleniumScript.toAbsolutePath(), bkFile);

    try {
      Files.move(seleniumScript, bkFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void openTestScripts(List<Path> testScripts) {
    for (Path testScript : testScripts) {
      try {
        Desktop.getDesktop().open(testScript.toFile());
      } catch (IOException e) {
        log.error("open.script.error", e);
      }
    }
  }

  private Path getOutputDirPath() {
    return Paths.get(getProjectDirectory(), outputDir);
  }

  private String getProjectDirectory() {
    String dir = System.getProperty("sitwt.projectDirectory");
    return StringUtils.isEmpty(dir) ? "." : dir;
  }

  private List<SeleniumTestScript> loadSeleniumScripts(Path script) {
    List<SeleniumTestScript> scripts = new ArrayList<>();

    JsonNode node = JsonUtils.readTree(script);
    String baseUrl = node.get("url").asText();

    for (JsonNode testNode : node.get("tests")) {
      scripts.add(loadSeleniumScript(testNode, baseUrl));
    }

    return scripts;
  }

  private SeleniumTestScript loadSeleniumScript(JsonNode testNode, String baseUrl) {
    SeleniumTestScript script = new SeleniumTestScript();
    script.setBaseUrl(baseUrl);
    script.setName(testNode.get("name").asText());

    for (JsonNode commandNode : testNode.get("commands")) {

      SeleniumTestStep testStep = new SeleniumTestStep();

      testStep.setCommand(commandNode.get("command").asText());
      testStep.setTarget(commandNode.get("target").asText());
      testStep.setValue(commandNode.get("value").asText());

      script.getTestStepList().add(testStep);

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

  public String getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
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
