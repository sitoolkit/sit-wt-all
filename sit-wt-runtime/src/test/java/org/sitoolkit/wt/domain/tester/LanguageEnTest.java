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
package org.sitoolkit.wt.domain.tester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.evidence.EvidenceManager;
import org.sitoolkit.wt.infra.ApplicationContextHelper;

/**
 *
 * @author tsunami.nakano
 */
public class LanguageEnTest extends SitTesterTestBase {

    @Resource
    EvidenceManager em;

    @Resource
    WebDriver driver;

    private String language = Locale.getDefault().getLanguage();

    @Override
    public void setUp() {

    }

    @Override
    public void tearDown() {

    }

    private void setUp(String testScriptPath) {
        // we must get tester instance from application context instead of field
        // injection
        // because field injection with surefire/failsafe parallel execution
        // doesn't work.
        // https://jira.spring.io/browse/SPR-12421
        tester = ApplicationContextHelper.getBean(Tester.class);

        log.trace("setup", new Object[] { this, testName.getMethodName(), tester });
        tester.prepare(testScriptPath, getSheetName(), getCurrentCaseNo());

        TestEventListener listener = ApplicationContextHelper.getBean(TestEventListener.class);
        listener.before();
    }

    private void after() {
        TestEventListener listener = ApplicationContextHelper.getBean(TestEventListener.class);
        listener.after();

        tester.tearDown();
    }

    @Test
    public void downloadOperationTest001() throws FileNotFoundException, IOException {

        if (language == "en") {

            String testScriptPath = "src/test/resources/language-en-test/DownloadOperationTestScript.xlsx";
            setUp(testScriptPath);
            test();
            File targetFile = new File("src/main/webapp/pdf/DownloadTest.pdf");
            String targetHash = DigestUtils.md5Hex(new FileInputStream(targetFile));

            String testScriptName = StringUtils.substringAfterLast(testScriptPath, "/");
            String caseNo = "001";
            String baseFileName = targetFile.getName();
            File firstEvidence = em.buildDownloadFile(testScriptName, caseNo, "2", "参考資料",
                    baseFileName);
            File secondEvidence = em.buildDownloadFile(testScriptName, caseNo, "5", "参考資料表示",
                    baseFileName);

            assertThat(DigestUtils.md5Hex(new FileInputStream(firstEvidence)), is(targetHash));
            assertThat(DigestUtils.md5Hex(new FileInputStream(secondEvidence)), is(targetHash));
            after();
        }

    }

    @Test
    public void drawTest001() {
        if (language == "en") {
            setUp("src/test/resources/language-en-test/DrawTestScript.xlsx");
            test();
        }

    }

    @Test
    public void fileUploadTest001() {
        if (language == "en") {
            setUp("src/test/resources/language-en-test/FileUploadTestScript.xlsx");
            test();
        }
    }

    @Test
    public void goToTest001() {
        if (language == "en") {
            setUp("src/test/resources/language-en-test/GoToTestScript.xlsx");
            test();
        }
    }

    @Test
    public void includeOuterTest001() {
        if (language == "en") {
            setUp("src/test/resources/language-en-test/IncludeOuterTestScript.xlsx");
            test();
            WebElement remark = driver.findElement(By.id("remark"));
            assertThat(remark.getAttribute("value"), is("IncludeOuterのテスト001"));
        }
    }

    @Test
    public void includeOuterTest002() {
        if (language == "en") {
            setUp("src/test/resources/language-en-test/IncludeOuterTestScript.xlsx");
            test();
            WebElement remark = driver.findElement(By.id("remark"));
            assertThat(remark.getAttribute("value"), is("IncludeOuterのテスト002"));
        }
    }

    @Test
    public void storeElementIndexTest001() {
        if (language == "en") {
            setUp("src/test/resources/language-en-test/StoreElementIndexTestScript.xlsx");
            test();
        }
    }

    @Test
    public void storeElementValueTest001() {
        if (language == "en") {
            setUp("src/test/resources/language-en-test/StoreElementValueTestScript.xlsx");
            test();
        }
    }

    @Test
    public void switchFrameTest001() {
        if (language == "en") {
            setUp("src/test/resources/language-en-test/SwitchFrameTestScript.xlsx");
            test();
        }
    }

    @Test
    public void verifyNGTest001() {
        if (language == "en") {
            setUp("src/test/resources/language-en-test/VerifyNGTestScript.xlsx");
            TestResult result = tester.operate("001");
            assertEquals(3, result.getFailCount());
        }
    }

    @Override
    protected String getCurrentCaseNo() {
        return StringUtils.substringAfter(testName.getMethodName(), "Test");
    }

    @Override
    protected String getTestScriptPath() {
        return "";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
