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
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
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

    private static final SitLogger LOG = SitLoggerFactory
            .getLogger(WebDriverMethodInterceptor.class);

    private WebElementExceptionChecker checker;

    public WebDriverMethodInterceptor(WebElementExceptionChecker checker) {
        super();
        this.checker = checker;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {

        Method method = mi.getMethod();

        if (!"findElement".equals(method.getName())) {
            return mi.proceed();
        }

        Object ret = mi.proceed();

        if (ret == null) {
            LOG.debug("restart", mi.getThis().getClass().getName(), method.getName());
            ret = mi.proceed();
        }

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(ret.getClass());
        proxyFactory.addAdvice(new WebElementMethodInterceptor(mi, checker));
        proxyFactory.setTarget(ret);

        return proxyFactory.getProxy();

    }

}
