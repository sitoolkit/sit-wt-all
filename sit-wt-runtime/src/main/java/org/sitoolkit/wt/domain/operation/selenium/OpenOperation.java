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

import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.PropertyManager;
import org.sitoolkit.wt.infra.SitPathUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class OpenOperation extends SeleniumOperation {

    @Resource
    PropertyManager pm;

    /**
     * テストステップで指定されたURLを開きます。 URLはロケーターの値とシステムプロパティ<code>baseUrl</code>
     * を連結した文字列です。 ロケーターの値がhttp(s)で始まる場合は<code>baseUrl</code>は無視されます。 ロケーターの値、
     * <code>baseUrl</code>何れもhttp(s)で始まらない場合は、 ファイルプロトコルのURLとして解釈します。
     *
     *
     * @see WebDriver#get(String)
     */
    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        String url = SitPathUtils.buildUrl(pm.getBaseUrl(), testStep.getLocator().getValue());
        ctx.info("URLをオープンします {}", url);
        seleniumDriver.get(url);
    }

}
