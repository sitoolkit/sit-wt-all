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

import javax.annotation.Resource;

import org.sitoolkit.wt.domain.evidence.LogRecord;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class GotoOperation implements Operation {

    protected SitLogger log = SitLoggerFactory.getLogger(getClass());
    @Resource
    protected TestContext current;
    // @Resource
    // protected OperationLog opelog;

    @Override
    public OperationResult operate(TestStep testStep) {
        String value = testStep.getValue();

        LogRecord record = LogRecord.info(log, testStep, "test.step.execute", value);

        TestScript testScript = current.getTestScript();
        int nextIndex = testScript.getIndexByScriptNo(value) - 1;
        current.setCurrentIndex(nextIndex);

        return new OperationResult(record);
    }

}
