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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class KeyOperation extends SeleniumOperation {

    private static final String SPECIAL_KEY_PREFIX = "key_";

    public void execute(TestStep testStep) {
        WebElement element = findElement(testStep.getLocator());
        info(Arrays.toString(testStep.getValues()), "打鍵", element);
        CharSequence[] chars = split(testStep.getValues());
        Keys.chord(chars);
    }

    CharSequence[] split(String[] values) {
        List<CharSequence> chars = new ArrayList<CharSequence>();
        for (String str : values) {
            if (str.startsWith(SPECIAL_KEY_PREFIX)) {
                str = StringUtils.substringAfter(str, SPECIAL_KEY_PREFIX);
                chars.add(Keys.valueOf(str.toUpperCase()));
            } else {
                chars.add(str);
            }
        }
        return chars.toArray(new CharSequence[chars.size()]);
    }
}
