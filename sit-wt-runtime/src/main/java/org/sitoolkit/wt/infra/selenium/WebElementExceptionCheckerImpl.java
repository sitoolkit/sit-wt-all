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

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;

public class WebElementExceptionCheckerImpl implements WebElementExceptionChecker {

    @Override
    public boolean isRetriable(Throwable throwable) {

        if (throwable instanceof StaleElementReferenceException) {
            return true;

        } else if (throwable instanceof ElementNotVisibleException) {
            return true;

        } else if (throwable instanceof WebDriverException) {

            if (StringUtils.startsWith(throwable.getMessage(),
                    "Error determining if element is displayed")) {
                return true;
            }
        }

        return false;
    }

}
