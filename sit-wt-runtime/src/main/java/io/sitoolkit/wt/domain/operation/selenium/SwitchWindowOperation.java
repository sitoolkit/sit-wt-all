/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.sitoolkit.wt.domain.operation.selenium;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.NoSuchWindowException;
import org.springframework.stereotype.Component;
import io.sitoolkit.wt.domain.testscript.Locator;
import io.sitoolkit.wt.domain.testscript.TestStep;
import io.sitoolkit.wt.infra.resource.MessageManager;

/**
 * このクラスは、ブラウザのウィンドウを切り替える操作を実行します。
 *
 * @author yuichi.kuwahara
 */
@Component
public class SwitchWindowOperation extends SeleniumOperation {

  @Override
  public void execute(TestStep testStep, SeleniumOperationContext ctx) {
    Locator windowLocator = testStep.getLocator();

    if (Locator.Type.title == windowLocator.getTypeVo()) {
      List<String> avairableName = new ArrayList<>();
      List<String> avairableTitle = new ArrayList<>();

      for (String windowHandle : seleniumDriver.getWindowHandles()) {
        String windowTitle = seleniumDriver.switchTo().window(windowHandle).getTitle();

        avairableName.add(windowHandle);
        avairableTitle.add(windowTitle);

        if (windowTitle.equals(windowLocator.getValue())) {
          ctx.info("window.switch", windowLocator);
          return;
        }
      }

      throw new NoSuchWindowException(MessageManager.getMessage("window.no.such.error",
          windowLocator, avairableName, avairableTitle));

    } else {
      ctx.info("window.switch", windowLocator);
      String windowName = windowLocator.getValue();
      if ("_parent".equalsIgnoreCase(windowName) || "null".equalsIgnoreCase(windowName)) {
        windowName = "";
      }
      seleniumDriver.switchTo().window(windowName);

    }
  }

}
