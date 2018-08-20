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
package org.sitoolkit.wt.app.pagespec2script;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.app.OperationExecutor;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;
import org.sitoolkit.wt.infra.ApplicationContextHelper;

/**
 *
 * @author yuichi.kuwahara
 */
public class PageSpec2ScriptTest extends SitTesterTestBase {

    @Rule
    public TestName name;

    @Before
    @Override
    public void setUp() {
        File testScript = new File("pageobj/入力TestScript.csv");
        if (testScript.exists()) {
            testScript.delete();
        }
        PageSpec2Script pagespec2script = PageSpec2Script.initInstance();
        testScript = pagespec2script
                .convert(new File(pagespec2script.getPagespecDir(), "画面定義書_入力.xlsx"));
        testScript.deleteOnExit();

        super.setUp();
    }

    @Test
    public void test001() {
        OperationExecutor.execute(ApplicationContextHelper.getApplicationContext(), "open",
                "input.html");
        test();
    }

    @Override
    public void tearDown() {
        WebDriver driver = ApplicationContextHelper.getBean(WebDriver.class);
        Alert alert = driver.switchTo().alert();
        alert.accept();
        super.tearDown();
    }

    @Override
    protected String getTestScriptPath() {
        return new File(new PageSpec2Script().getTestScriptDir(), "入力TestScript.csv")
                .getAbsolutePath();
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}