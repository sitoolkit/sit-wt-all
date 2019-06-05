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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import io.sitoolkit.wt.app.sample.SampleGenerator;
import io.sitoolkit.wt.domain.tester.TestBase;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.infra.ApplicationContextHelper;

/**
 *
 * @author yuichi.kuwahara
 */
public class Selenium2ScriptTest extends TestBase {

  private String testScriptPath;

  private static String TARGET_SCRIPT = "testscript/SeleniumIDETestScript.side";

  private static String BACKUPED_SCRIPT = TARGET_SCRIPT + ".bk";

  @BeforeClass
  public static void generateSample() {
    SampleGenerator.generate();
  }

  @Before
  @Override
  public void setUp() {
    File outputFile1 = new File("testscript/SeleniumIDETestScript_Sample_Case_1.csv");
    File outputFile2 = new File("testscript/SeleniumIDETestScript_Sample_Case_2.csv");
    deleteFile(outputFile1);
    deleteFile(outputFile2);

    Selenium2Script converter = Selenium2Script.initInstance();
    converter.setOpenScript(false);
    converter.execute();

    assertThat("バックアップされたSeleniumScriptファイル", new File(BACKUPED_SCRIPT).exists(), is(true));

    TestScriptDao dao = ApplicationContextHelper.getBean(TestScriptDao.class);
    TestScript testScript1 = dao.load(outputFile1, getSheetName(), false);
    TestScript testScript2 = dao.load(outputFile2, getSheetName(), false);

    assertThat("Case1 step count", testScript1.getTestStepCount(), is(19));
    assertThat("Case1 step count", testScript1.getIndexByScriptNo("22"), is(18));
    assertThat("Case1 step count", testScript1.getIndexByScriptNo("23"), is(-1));
    assertThat("Case2 step count", testScript2.getTestStepCount(), is(8));
    assertThat("Case2 step count", testScript2.getIndexByScriptNo("9"), is(7));
    assertThat("Case2 step count", testScript2.getIndexByScriptNo("10"), is(-1));

    testScriptPath = outputFile1.getAbsolutePath();

    outputFile1.deleteOnExit();
    outputFile2.deleteOnExit();

    super.setUp();
  }

  private void deleteFile(File file) {
    if (file.exists()) {
      file.delete();
    }
  }

  @Test
  public void test001() {
    test();
  }

  @After
  @Override
  public void tearDown() {
    File seleniumScript = new File(BACKUPED_SCRIPT);
    seleniumScript.renameTo(new File(TARGET_SCRIPT));

    super.tearDown();
  }

  @Override
  protected String getTestScriptPath() {
    return testScriptPath;
  }

  @Override
  protected String getSheetName() {
    return "TestScript";
  }
}
