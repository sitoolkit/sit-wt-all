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
package io.sitoolkit.wt.domain.operation.selenium;

import java.io.File;
import java.net.URL;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.sitoolkit.wt.domain.evidence.EvidenceManager;
import io.sitoolkit.wt.domain.evidence.MessagePattern;
import io.sitoolkit.wt.domain.tester.TestContext;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.TestException;
import io.sitoolkit.wt.infra.resource.MessageManager;
import io.sitoolkit.wt.util.app.proxysetting.ProxySettingService;

/**
 *
 * @author takuya.kumakura
 */
@Component("downloadOperation")
public class DownloadOperation extends SeleniumOperation {

    @Resource
    TestContext current;

    @Resource
    EvidenceManager em;

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {

        String urlString = (testStep.getLocator().isNa()) ? seleniumDriver.getCurrentUrl()
                : findElement(testStep.getLocator()).getAttribute("href");

        File downloadFile = em.buildDownloadFile(current.getScriptName(), current.getCaseNo(),
                current.getTestStepNo(), current.getItemName(),
                StringUtils.substringAfterLast(urlString, "/"));

        ctx.info(MessagePattern.項目をXXします_URL_エビデンス, MessageManager.getMessage("download"),
                urlString, downloadFile.getAbsolutePath());

        try {
            URL targetUrl = new URL(urlString);

            ProxySettingService.getInstance().loadProxy();
            FileUtils.copyURLToFile(targetUrl, downloadFile);

        } catch (Exception exp) {
            throw new TestException(exp);
        }
    }
}
