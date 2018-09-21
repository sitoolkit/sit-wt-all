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
package io.sitoolkit.wt.domain.operation;

import java.io.File;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.sitoolkit.wt.domain.evidence.LogRecord;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.domain.tester.TestContextListener;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.PropertyManager;
import io.sitoolkit.wt.infra.TestException;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;
import io.sitoolkit.wt.infra.resource.MessageManager;

/**
 *
 * @author yu.kawai
 */
@Component
public class IncludeOperation implements Operation, TestContextListener {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(IncludeOperation.class);

    private String sheetName = "TestScript";

    @Resource
    PropertyManager pm;

    @Resource
    ApplicationContext appCtx;

    @Resource
    TestContext current;

    @Resource
    TestScriptDao dao;

    // @Resource
    // OperationLog opelog;

    @Override
    public OperationResult operate(TestStep testStep) {
        String testStepName = testStep.getLocator().getValue();

        LogRecord log = LogRecord.info(LOG, testStep, "script.execute", testStepName);

        current.backup();

        TestScript testScript = dao.load(new File(pm.getPageScriptDir(), testStepName), sheetName,
                false);

        current.setTestScript(testScript);
        current.reset();
        current.setCurrentIndex(current.getCurrentIndex() - 1);
        String caseNo = testStep.getValue();
        if (testScript.containsCaseNo(caseNo)) {
            current.setCaseNo(caseNo);
        } else {
            String msg = MessageManager.getMessage("case.number.error", caseNo)
                    + testScript.getCaseNoMap().keySet();
            throw new TestException(msg);
        }
        current.setTestContextListener(this);

        return new OperationResult(log);
    }

    @Override
    public void onEnd(TestContext testContext) {
        testContext.restore();
        testContext.setTestContextListener(null);
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

}
