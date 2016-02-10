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

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.LogRecord;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.TestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author yu.kawai
 */
@Component
public class ExecOperation implements Operation {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private static final String CMD_SEPARATOR = "[\\s]+";

    // @Resource
    // protected OperationLog opelog;

    @Override
    public OperationResult operate(TestStep testStep) {
        OperationResult result = new OperationResult();

        String cmd = testStep.getLocator().getValue();
        result.addRecord(LogRecord.info(log, testStep, "コマンド[{}]を実行します", cmd));

        ProcessBuilder pb = new ProcessBuilder(cmd.split(CMD_SEPARATOR));
        pb.redirectErrorStream(true);
        Process process = null;
        String cmdlog = null;
        InputStream is = null;
        int exitValue = 0;

        try {
            process = pb.start();
            process.waitFor();
            is = process.getInputStream();
            cmdlog = IOUtils.toString(is);
            exitValue = process.exitValue();
        } catch (Exception e) {
            throw new TestException(e);
        } finally {
            IOUtils.closeQuietly(is);
            process.destroy();
        }

        if (!StringUtils.isEmpty(cmdlog)) {
            result.addRecord(LogRecord.info(log, testStep, "コマンド実行結果 {}", cmdlog));
        }

        if (exitValue != 0) {
            throw new TestException("コマンドが異常終了しました。 " + cmd);
        }

        return result;
    }
}
