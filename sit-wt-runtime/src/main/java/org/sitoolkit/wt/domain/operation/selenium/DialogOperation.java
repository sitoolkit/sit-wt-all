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

import org.openqa.selenium.Alert;
import org.sitoolkit.wt.domain.testscript.TestStep;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class DialogOperation extends SeleniumOperation {

    public void execute(TestStep testStep) {
        try {
            Alert alert = seleniumDriver.switchTo().alert();
            String alertText = alert.getText();
            testStep.getLocator().setValue(alertText);

            if (testStep.getDialogValue()) {
                info("許諾", null);
                alert.accept();
            } else {
                info("拒否", null);
                alert.dismiss();
            }
        } catch (UnsupportedOperationException e) {
            log.warn("ドライバ{}はダイアログを操作できません。", this.getClass());
        }
    }
}
