package io.sitoolkit.wt.domain.operation.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.sitoolkit.wt.domain.tester.TestBase;
import io.sitoolkit.wt.domain.tester.TestResult;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class CommentOperationTest extends TestBase {

  @Test
  public void test001() throws IOException {
    TestResult result = tester.operate(getCurrentCaseNo());
    Path evidenceFile = result.getEvidenceFile();

    String evidenceHtml =
        FileUtils.readFileToString(evidenceFile.toFile(), Charset.defaultCharset());

    Pattern pattern = Pattern.compile("^\\s*<td>\\[.*\\] コメント： (.*)$", Pattern.MULTILINE);
    Matcher matcher = pattern.matcher(evidenceHtml);
    List<String> results = matcher.results().map((m) -> m.group(1)).collect(Collectors.toList());

    assertThat(results.size(), is(3));
    assertThat(results.get(0), is("コメント1"));
    assertThat(results.get(1), is("コメント2"));
    assertThat(results.get(2), is("コメント3"));
  }

  @Override
  protected String getTestScriptPath() {
    return "src/test/resources/selenium/CommentTestScript.csv";
  }

  @Override
  protected String getSheetName() {
    return "TestScript";
  }
}
