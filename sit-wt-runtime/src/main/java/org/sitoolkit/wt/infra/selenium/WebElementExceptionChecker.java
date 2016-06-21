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

/**
 * {@code WebElementMethodInterceptor}で再実行を行うべきか否かを判定するインターフェースです。
 * 
 * @see WebElementMethodInterceptor
 * @author yuichi.kuwahara
 *
 */
public interface WebElementExceptionChecker {

    /**
     * 例外が{@code WebElementMethodInterceptor}で再実行を行うべきものである場合にtrueを返します。
     * 
     * @param exception
     *            検査対象の例外
     * @return 例外が{@code WebElementMethodInterceptor}で再実行を行うべきものである場合にtrue
     */
    boolean isRetriable(Exception exception);

}
