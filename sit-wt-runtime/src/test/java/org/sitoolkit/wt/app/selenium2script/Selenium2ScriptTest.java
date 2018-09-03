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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;

/**
 *
 * @author yuichi.kuwahara
 */
public class Selenium2ScriptTest extends SitTesterTestBase {

    private String testScriptPath;

    private static String TARGET_SCRIPT = "testscript/SeleniumIDETestScript.html";

    private static String BACKUPED_SCRIPT = TARGET_SCRIPT + ".bk";

    @Before
    @Override
    public void setUp() {
        File testScript = new File("testscript/SeleniumIDETestScript.csv");
        if (testScript.exists()) {
            testScript.delete();
        }

        Selenium2Script converter = Selenium2Script.initInstance();
        converter.setOpenScript(false);
        int ret = converter.execute();

        assertThat("実行結果コード", ret, is(0));
        assertThat("バックアップされたSeleniumScriptファイル", new File(BACKUPED_SCRIPT).exists(), is(true));

        testScriptPath = testScript.getAbsolutePath();

        testScript.deleteOnExit();

        super.setUp();
    }

    @Test
    public void test001() {
        test();
    }

    @After
    @Override
    public void tearDown() {
        super.tearDown();

        File seleniumScript = new File(BACKUPED_SCRIPT);
        seleniumScript.renameTo(new File(TARGET_SCRIPT));
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