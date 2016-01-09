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

import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.sitoolkit.wt.domain.evidence.OperationLog;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class GotoOperation implements Operation {

    protected Logger log = LoggerFactory.getLogger(getClass());
    @Resource
    protected TestContext current;
    @Resource
    protected OperationLog opelog;

    @Override
    public void execute(TestStep testStep) {
        String value = testStep.getValue();

        opelog.info(log, ElementPosition.EMPTY, "ステップNo[{}]を実行します。", value);

        TestScript testScript = current.getTestScript();
        int nextIndex = testScript.getIndexByScriptNo(value) - 1;
        current.setCurrentIndex(nextIndex);

    }

}
