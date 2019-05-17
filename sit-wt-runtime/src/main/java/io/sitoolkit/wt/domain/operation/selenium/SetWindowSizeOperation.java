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

import org.openqa.selenium.Dimension;
import org.springframework.stereotype.Component;
import io.sitoolkit.wt.domain.testscript.TestStep;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class SetWindowSizeOperation extends SeleniumOperation {

  @Override
  public void execute(TestStep testStep, SeleniumOperationContext ctx) {
    String value = testStep.getValue();
    String[] size = value.split(",");

    if (size.length != 2) {
      log.warn("window.sizen.set.error");
      return;
    }

    try {
      int width = Integer.parseInt(size[0]);
      int height = Integer.parseInt(size[1]);

      ctx.info("window.size.set", width, height);

      Dimension targetSize = new Dimension(width, height);
      seleniumDriver.manage().window().setSize(targetSize);
    } catch (NumberFormatException e) {
      log.warn("window.sizen.set.error", e);
    }
  }
}
