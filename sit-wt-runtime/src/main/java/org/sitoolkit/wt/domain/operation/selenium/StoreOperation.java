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
package org.sitoolkit.wt.domain.operation.selenium;

import javax.annotation.Resource;

import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 * テストスクリプト内で使用可能な変数を格納する操作です。
 *
 * @author yuichi.kuwahara
 */
@Component
public class StoreOperation extends SeleniumOperation {

    @Resource
    TestContext context;

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        String name = testStep.getLocator().getValue();
        String value = testStep.getValue();

        log.info("var.define", name, value);

        context.addParam(name, value);
    }

}
