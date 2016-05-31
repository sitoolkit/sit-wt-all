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

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.sitoolkit.util.tabledata.TableDataMapper;
import org.sitoolkit.wt.domain.tester.SitTesterTestBase;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 *
 * @author yuichi.kuwahara
 */
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class,
        Selenium2ScriptTest.class })
public class Selenium2ScriptTest extends SitTesterTestBase {

    private String testScriptPath;

    @Resource
    ApplicationContext appCtx;

    @Before
    @Override
    public void setUp() {
        Selenium2Script converter = Selenium2Script.initInstance();
        File testScript = converter.convert(new File("seleniumscript/SeleniumIDETestScript.html"));
        testScriptPath = testScript.getAbsolutePath();

        // Commons BeanutilsのConverterが異なるApplicationContext間で共有となる事象の暫定対応
        TableDataMapper dm = appCtx.getBean(TableDataMapper.class);
        dm.initConverters();

        super.setUp();
    }

    @Test
    public void test001() {
        test();
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