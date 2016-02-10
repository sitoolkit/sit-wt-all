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
package org.sitoolkit.wt.domain.operation;

import java.io.File;

import javax.annotation.Resource;

import org.sitoolkit.wt.domain.evidence.LogRecord;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.tester.TestContextListener;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.TestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author yu.kawai
 */
@Component
public class IncludeOperation implements Operation, TestContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(IncludeOperation.class);

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

        LogRecord log = LogRecord.info(LOG, testStep, "テストスクリプト[{}]を実行します。", testStepName);

        current.backup();

        TestScript testScript = dao.load(new File(pm.getPageObjectDir(), testStepName), sheetName,
                false);

        current.setTestScript(testScript);
        current.reset();
        current.setCurrentIndex(current.getCurrentIndex() - 1);
        String caseNo = testStep.getValue();
        if (testScript.containsCaseNo(caseNo)) {
            current.setCaseNo(caseNo);
        } else {
            String msg = "指定されたケース番号[" + caseNo + "]は不正です。指定可能なケース番号："
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
