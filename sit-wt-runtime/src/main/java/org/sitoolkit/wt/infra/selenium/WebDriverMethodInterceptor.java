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

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;

/**
 * WebDriverに次の機能を追加するMethodInterceptorです。
 *
 * <ul>
 * <li>WebDriver.findElementでnullが返ってきた場合に再実行する
 * <li>WebDriver.findElementの戻り値のWebElementをプロキシする
 * </ul>
 *
 * @author yuichi.kuwahara
 *
 */
public class WebDriverMethodInterceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverMethodInterceptor.class);

    private WebElementExceptionChecker checker;

    public WebDriverMethodInterceptor(WebElementExceptionChecker checker) {
        super();
        this.checker = checker;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {

        Method method = mi.getMethod();

        if (!"findElement".equals(method.getName())) {
            return proceed(mi);
        }

        Object ret = proceed(mi);

        if (ret == null) {
            LOG.debug("{} {}でnullが返りました 再実行します", mi.getThis().getClass().getName(),
                    method.getName());
            ret = proceed(mi);
        }

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(ret.getClass());
        proxyFactory.addAdvice(new WebElementMethodInterceptor(mi, checker));
        proxyFactory.setTarget(ret);

        return proxyFactory.getProxy();

    }

    /**
     * {@code MethodInvocation}を実行します。 {@link MethodInvocation}の対象オブジェクトがAOP
     * Proxyされている場合、対象メソッドをリフレクションで実行します。 そうでない場合、
     * {@code MethodInvocation#proceed()}を実行します。
     * 
     * @param mi
     *            {@code MethodInvocation}インスタンス
     * @return {@code MethodInvocation}インスタンスの実行結果
     * @throws Throwable
     */
    private Object proceed(MethodInvocation mi) throws Throwable {
        // Object target = mi.getThis();
        // if (AopUtils.isAopProxy(target)) {
        // return AopUtils.invokeJoinpointUsingReflection(target,
        // mi.getMethod(),
        // mi.getArguments());
        // } else {
        return mi.proceed();
        // }
    }

}
