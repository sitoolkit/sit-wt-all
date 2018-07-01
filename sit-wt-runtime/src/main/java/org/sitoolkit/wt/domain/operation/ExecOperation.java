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
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.evidence.LogRecord;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.TestException;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.sitoolkit.wt.infra.resource.MessageManager;
import org.springframework.stereotype.Component;

/**
 *
 * @author yu.kawai
 */
@Component
public class ExecOperation implements Operation {

    protected SitLogger log = SitLoggerFactory.getLogger(getClass());

    private static final String CMD_SEPARATOR = "[\\s]+";

    private static boolean OS_IS_WINDOWS = System.getProperty("os.name").toLowerCase()
            .startsWith("windows");

    @Override
    public OperationResult operate(TestStep testStep) {
        OperationResult result = new OperationResult();

        String command = testStep.getLocator().getValue();

        if (OS_IS_WINDOWS) {
            command = "cmd /c " + command;
        }

        result.addRecord(LogRecord.info(log, testStep, "cmd.execute", command));

        ProcessBuilder pb = new ProcessBuilder(command.split(CMD_SEPARATOR));
        pb.redirectErrorStream(true);
        Process process = null;
        String cmdlog = null;
        InputStream is = null;
        int exitValue = 0;

        try {
            process = pb.start();
            process.waitFor();
            is = process.getInputStream();
            cmdlog = IOUtils.toString(is, Charset.defaultCharset());
            exitValue = process.exitValue();
        } catch (Exception e) {
            throw new TestException(e);
        } finally {
            IOUtils.closeQuietly(is);
            if (process != null) {
                process.destroy();
            }
        }

        if (!StringUtils.isEmpty(cmdlog)) {
            result.addRecord(LogRecord.info(log, testStep, "cmd.result", cmdlog));
        }

        if (exitValue != 0) {
            throw new TestException(MessageManager.getMessage("cmd.exception") + command);
        }

        return result;
    }
}
