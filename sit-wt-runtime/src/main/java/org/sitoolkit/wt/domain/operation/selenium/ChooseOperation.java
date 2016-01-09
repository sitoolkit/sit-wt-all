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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 * このクラスは、ラジオボタンとチェックボックスの選択操作を行います。 使用例として、チェックボックス初期状態と
 * テストデータで定義された操作を実行した後の状態を以下に示します。
 *
 * <pre>
 * ケース1：off→on
 *   初期状態
 *     □運動 □芸術 □仕事
 *
 *   テストデータ
 *     運動;仕事
 *
 *   操作後状態
 *     ■運動 □芸術 ■仕事
 *
 * ケース2：on→on テストデータに定義する
 *   初期状態
 *     ■運動 □芸術 □仕事
 *
 *   テストデータ
 *     運動;仕事
 *
 *   操作後状態
 *     ■運動 □芸術 ■仕事
 *
 * ケース3：on→on テストデータに定義しない
 *   初期状態
 *     □運動 ■芸術 □仕事
 *
 *   テストデータ
 *     運動;仕事
 *
 *   操作後状態
 *     ■運動 ■芸術 ■仕事
 *
 * ケース4:on→off
 *   初期状態
 *     ■運動 □芸術 □仕事
 *
 *   テストデータ
 *     運動_off;仕事
 *
 *   操作後状態
 *     □運動 □芸術 ■仕事
 *
 * </pre>
 *
 * @author yuichi.kuwahara
 */
@Component
public class ChooseOperation extends SeleniumOperation {

    public void execute(TestStep testStep) {
        String[] values = testStep.getValues();
        info(Arrays.toString(values), "選択", null);
        Map<String, Choice> map = toMap(values);
        if ("label".equals(testStep.getDataType())) {
            chooseByLabel(testStep.getLocator(), map);
        } else {
            chooseByValue(testStep.getLocator(), map);
        }
    }

    protected void chooseByLabel(Locator locator, Map<String, Choice> map) {
        for (WebElement element : findElements(locator)) {
            String id = element.getAttribute("id");
            WebElement label = seleniumDriver
                    .findElement(By.cssSelector("label[for='" + id + "']"));
            String labelText = StringUtils.trim(label.getText());

            Choice choice = map.get(labelText);
            if (choice == null) {
                continue;
            }
            if (setChecked(element, label, choice.on)) {
                addPosition(element);
            }
        }
    }

    protected void chooseByValue(Locator locator, Map<String, Choice> map) {
        for (WebElement element : findElements(locator)) {
            Choice choice = map.get(element.getAttribute("value"));
            if (choice == null) {
                continue;
            }
            if (setChecked(element, element, choice.on)) {
                addPosition(element);
            }
        }
    }

    protected Map<String, Choice> toMap(String[] values) {
        Map<String, Choice> map = new HashMap<String, Choice>();

        for (String value : values) {
            Choice choice = new Choice();
            int idx = value.indexOf("_off");
            if (idx < 0) {
                choice.value = value;
                choice.on = true;
            } else {
                choice.value = value.substring(0, idx);
                choice.on = false;
            }
            map.put(choice.value, choice);
        }

        return map;
    }

    protected class Choice {
        protected String value;
        protected boolean on;
    }
}
