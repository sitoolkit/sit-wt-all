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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class DrawLineOperation extends SeleniumOperation {

    @Override
    public void execute(TestStep testStep, SeleniumOperationContext ctx) {
        WebElement canvas = findElement(testStep.getLocator());
        Actions builder = new Actions(seleniumDriver);

        int fromX, fromY, toX, toY;
        String[] values = testStep.getValues();
        if (values.length == 2) {
            toX = Integer.parseInt(values[0]);
            toY = Integer.parseInt(values[1]);
            ctx.info(canvas, "キャンバス({})の({}, {})まで線を引きます。", testStep.getLocator(), toX, toY);
        } else {
            fromX = Integer.parseInt(values[0]);
            fromY = Integer.parseInt(values[1]);
            toX = Integer.parseInt(values[2]);
            toY = Integer.parseInt(values[3]);

            ctx.info(canvas, "キャンバス({})の({}, {})から({}, {})まで線を引きます。", testStep.getLocator(), fromX,
                    fromY, toX, toY);
            builder.moveToElement(canvas, fromX, fromY);
        }

        Action drawLine = builder.clickAndHold().moveByOffset(toX, toY).release().build();
        drawLine.perform();
    }
}
