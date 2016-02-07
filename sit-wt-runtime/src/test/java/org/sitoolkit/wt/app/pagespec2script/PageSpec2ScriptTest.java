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

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.sitoolkit.util.tabledata.TableDataMapper;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 *
 * @author yuichi.kuwahara
 */
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class,
        PageSpec2ScriptTest.class })
public class PageSpec2ScriptTest extends SitTesterTestBase {

    @Rule
    public TestName name;

    @Resource
    ApplicationContext appCtx;

    PageSpec2Script converter;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        converter = PageSpec2Script.initInstance();
        File testScript = converter.convert(new File(converter.getPagespecDir(), "画面定義書_入力.xlsx"));
        testScript.deleteOnExit();

        // TODO Commons BeanutilsのConverterが異なるApplicationContext間で共有となる事象の暫定対応
        TableDataMapper dm = testContext.getApplicationContext().getBean(TableDataMapper.class);
        dm.initConverters();

        super.beforeTestClass(testContext);
    }

    @Test
    public void test001() {
        test();
    }

    @Override
    public void tearDown() {
        WebDriver driver = appCtx.getBean(WebDriver.class);
        Alert alert = driver.switchTo().alert();
        alert.accept();
        super.tearDown();
    }

    @Override
    protected String getTestScriptPath() {
        return new File(converter.getTestScriptDir(), "入力TestScript.xlsx").getAbsolutePath();
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}