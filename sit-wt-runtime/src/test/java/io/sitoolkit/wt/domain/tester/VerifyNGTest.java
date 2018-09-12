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

import static org.junit.Assert.*;

import org.junit.Test;

import io.sitoolkit.wt.domain.tester.SitTesterTestBase;
import io.sitoolkit.wt.domain.tester.TestResult;

/**
 *
 * @author yuichi.kuwahara
 */
public class VerifyNGTest extends SitTesterTestBase {

    @Test
    public void test001() {
        TestResult result = tester.operate("001");
        assertEquals(3, result.getFailCount());
    }

    @Override
    protected String getTestScriptPath() {
        return "src/test/resources/VerifyNGTestScript.csv";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }

}
