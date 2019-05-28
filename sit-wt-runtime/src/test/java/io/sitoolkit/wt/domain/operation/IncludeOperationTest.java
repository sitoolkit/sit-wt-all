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
package io.sitoolkit.wt.domain.operation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import javax.annotation.Resource;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.sitoolkit.wt.domain.tester.TestBase;

/**
 *
 * @author yu.kawai
 */
public class IncludeOperationTest extends TestBase {

  @Resource
  WebDriver driver;

  @Test
  public void test001() {
    test();

    WebElement remark = driver.findElement(By.id("remark"));
    assertThat(remark.getAttribute("value"), is("IncludeOuterのテスト001"));
  }

  @Test
  public void test002() {
    test();

    WebElement remark = driver.findElement(By.id("remark"));
    assertThat(remark.getAttribute("value"), is("IncludeOuterのテスト002"));
  }

  @Override
  protected String getTestScriptPath() {
    return "src/test/resources/IncludeOuterTestScript.csv";
  }

  @Override
  protected String getSheetName() {
    return "TestScript";
  }

}
