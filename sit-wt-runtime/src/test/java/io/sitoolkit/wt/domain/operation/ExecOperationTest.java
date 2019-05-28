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

import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.sitoolkit.wt.domain.tester.TestBase;

/**
 *
 * @author yu.kawai
 */
public class ExecOperationTest extends TestBase {

  private static final String TEST_DIR = "./testdir";

  @Before
  public void before() {
    File dir = new File(TEST_DIR);
    if (dir.isDirectory()) {
      dir.delete();
    }
    super.setUp();
  }

  @Test
  public void test001() {
    test("001", new AfterTest() {

      @Override
      public void callback() {
        File dir = new File(TEST_DIR);
        assertTrue(dir.isDirectory());
      }

    });
  }

  @After
  public void after() {
    File dir = new File(TEST_DIR);
    if (dir.isDirectory())
      dir.delete();
  }

  @Override
  protected String getTestScriptPath() {
    return "src/test/resources/ExecOperationTestScript.csv";
  }

  @Override
  protected String getSheetName() {
    return "TestScript";
  }

}
