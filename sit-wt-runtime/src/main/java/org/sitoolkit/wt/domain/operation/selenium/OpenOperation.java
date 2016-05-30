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

import java.io.File;
import java.net.MalformedURLException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.sitoolkit.wt.domain.tester.TestContext;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.sitoolkit.wt.infra.TestException;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class OpenOperation extends SeleniumOperation {

    private static final String LOCAL_BASE_URL = "src/main/webapp";

    @Resource
    protected TestContext current;

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
        String url = buildUrl(System.getProperty("baseUrl"), testStep.getLocator().getValue());
        ctx.info("URLをオープンします {}", url);
        seleniumDriver.get(url);
    }

    /**
     * オープン先となるURLを構築します。
     *
     * @param baseUrl
     *            基準となるURL
     * @param path
     *            基準となるURLからの相対パス
     * @return オープン先となるURLの文字列
     */
    protected String buildUrl(String baseUrl, String path) {
        if (path.startsWith("http:") || path.startsWith("https:")) {
            return path;
        }
        if (StringUtils.isEmpty(baseUrl)) {
            return file2url(concatPath(LOCAL_BASE_URL, path));
        } else {
            if (baseUrl.startsWith("http:") || baseUrl.startsWith("https:")) {
                return concatPath(baseUrl, path);
            } else {
                return concatPath(file2url(baseUrl), path);
            }
        }
    }

    private String file2url(String path) {
        try {
            return new File(path).toURI().toURL().toString();
        } catch (MalformedURLException e) {
            throw new TestException(e);
        }
    }

    private String concatPath(String a, String b) {
        return a.endsWith("/") ? a + b : a + "/" + b;
    }
}
