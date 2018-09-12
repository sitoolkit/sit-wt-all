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
package io.sitoolkit.wt.infra;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author yuichi.kuwahara
 */
public class RegexHelper {

    /**
     * 
     * @param regex
     * @param value
     * @return 
     */
    public static boolean matches(String regex, String value) {
        String regexpStr = StringUtils.substringAfter(regex, "regexp:");
        if (StringUtils.isNotEmpty(regexpStr)) {
            return value.matches(regexpStr);
        } else {
            return regex.equals(value);
        }
    }
}
