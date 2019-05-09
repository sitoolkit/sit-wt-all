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
package io.sitoolkit.wt.domain.tester;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.sitoolkit.wt.app.config.RuntimeConfig;
import io.sitoolkit.wt.domain.testclass.TestClassNameConverter;
import io.sitoolkit.wt.infra.ApplicationContextHelper;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

/**
 *
 * @author yuichi.kuwahara
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuntimeConfig.class)
public abstract class SitTesterTestBase {

    protected SitLogger log = SitLoggerFactory.getLogger(getClass());

    @Rule
    public TestName testName = new TestName();

    protected Tester tester;

    protected String getCurrentCaseNo() {
        return TestClassNameConverter.method2caseNo(testName.getMethodName());
    }

    protected void test() {
        test(null);
    }

    protected void test(AfterTest afterTest) {
        test(getCurrentCaseNo(), afterTest);
    }

    protected void test(String caseNo, AfterTest afterTest) {
        TestResult result = tester.operate(caseNo);

        if (afterTest != null) {
            afterTest.callback();
        }

        if (!result.isSuccess()) {
            fail(result.buildReason());
        }
    }

    @Before
    public void setUp() {

        // we must get tester instance from application context instead of field
        // injection
        // because field injection with surefire/failsafe parallel execution
        // doesn't work.
        // https://jira.spring.io/browse/SPR-12421
        tester = ApplicationContextHelper.getBean(Tester.class);

        log.trace("setup", new Object[] { this, testName.getMethodName(), tester });
        tester.prepare(getTestScriptPath(), getSheetName(), getCurrentCaseNo());

        TestEventListener listener = ApplicationContextHelper.getBean(TestEventListener.class);
        listener.before();
    }

    @After
    public void tearDown() {
        TestEventListener listener = ApplicationContextHelper.getBean(TestEventListener.class);
        listener.after();

        tester.tearDown();
    }

    protected abstract String getTestScriptPath();

    protected abstract String getSheetName();

    protected interface AfterTest {
        void callback();
    }

}
