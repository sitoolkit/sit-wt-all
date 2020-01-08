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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import io.sitoolkit.wt.domain.evidence.EvidenceManager;
import io.sitoolkit.wt.domain.tester.TestBase;

/** @author takuya.kumakura */
public class DownloadOperationTest extends TestBase {

  @Resource EvidenceManager em;

  @Test
  public void test001() throws IOException {
    test();

    Path pdfPath = Paths.get("src/main/resources/webapp/pdf/DownloadTest.pdf");
    String pdfHash = DigestUtils.md5Hex(Files.newInputStream(pdfPath));

    Path htmlPath = Paths.get("src/main/resources/webapp/download.html");
    String htmlHash = DigestUtils.md5Hex(Files.newInputStream(htmlPath));

    String testScriptName = StringUtils.substringAfterLast(getTestScriptPath(), "/");
    String caseNo = "001";

    Path pdfEvidence =
        em.buildDownloadFile(
                testScriptName, caseNo, "2", "リンク先DL", pdfPath.getFileName().toString())
            .toPath();

    Path htmlEvidence =
        em.buildDownloadFile(
                testScriptName, caseNo, "3", "表示ページDL", htmlPath.getFileName().toString())
            .toPath();

    assertThat(DigestUtils.md5Hex(Files.newInputStream(pdfEvidence)), is(pdfHash));
    assertThat(DigestUtils.md5Hex(Files.newInputStream(htmlEvidence)), is(htmlHash));
  }

  @Override
  protected String getTestScriptPath() {
    return "src/test/resources/DownloadOperationTestScript.csv";
  }

  @Override
  protected String getSheetName() {
    return "TestScript";
  }
}
