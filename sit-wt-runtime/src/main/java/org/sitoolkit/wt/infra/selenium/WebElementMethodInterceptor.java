/*
 * Copyright 2016 Monocrea Inc.
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
package org.sitoolkit.wt.infra.selenium;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebElementの再実行機能を追加する{@code MethodInterceptor}です。
 * WebElementの任意のメソッド実行時に例外が発生した場合、 かつその例外が
 * {@code WebElementExceptionChecker#isRetriable(Exception)}でtrueの場合、
 * WebDriver.findElementを再実行して同じメソッドを実行します。
 * 
 * @author yuichi.kuwahara
 * @see WebElementExceptionChecker
 */
public class WebElementMethodInterceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(WebElementMethodInterceptor.class);

    /**
     * WebDriver#findElementのMethodInvocationインスタンス
     */
    private MethodInvocation webDriverFindElementInvocation;

    /**
     * webDriverFindElementInvocationの実行結果
     */
    private Object webElement = null;

    private WebElementExceptionChecker checker;

    /**
     * 
     * @param webDriverFindElementInvocation
     *            WebDriver#findElementのMethodInvocationインスタンス
     * @param checker
     *            {@code WebElementExceptionChecker}の実装クラスのインスタンス
     */
    public WebElementMethodInterceptor(MethodInvocation webDriverFindElementInvocation,
            WebElementExceptionChecker checker) {
        super();
        check(webDriverFindElementInvocation);
        this.webDriverFindElementInvocation = webDriverFindElementInvocation;
        this.checker = checker;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String className = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();

        LOG.trace("invoke method {} {}", className, methodName);

        try {
            if (webElement == null) {
                return invocation.proceed();
            } else {
                return invoke(invocation, webElement);
            }
        } catch (Exception e) {
            LOG.trace("invoke method {} {} throws exception {}",
                    new Object[] { className, methodName, e.getClass() });

            if (checker.isRetriable(e)) {
                LOG.trace("re-invoke method {} {}", className, methodName);

                webElement = webDriverFindElementInvocation.proceed();
                return invoke(invocation, webElement);
            }

            throw e;
        }
    }

    private Object invoke(MethodInvocation invocation, Object webElement) throws Throwable {
        return invocation.getMethod().invoke(webElement, invocation.getArguments());
    }

    private void check(MethodInvocation invocation) {

        if (!(invocation.getThis() instanceof WebDriver)) {
            throw new IllegalArgumentException(
                    invocation + "の対象オブジェクトはWebDriver型またはそのサブクラス型である必要があります");
        }

        String methodName = invocation.getMethod().getName();

        if (!"findElement".equals(methodName)) {
            throw new IllegalArgumentException(invocation + "はfindElementである必要があります。");
        }

    }

}
