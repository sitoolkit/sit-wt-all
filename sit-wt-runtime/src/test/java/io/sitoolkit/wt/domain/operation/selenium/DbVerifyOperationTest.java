package io.sitoolkit.wt.domain.operation.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import io.sitoolkit.wt.domain.tester.SitTesterTestBase;

/**
 *
 */
public class DbVerifyOperationTest extends SitTesterTestBase {

  @BeforeClass
  public static void runDerby() throws Exception {
    Connection conn = null;
    String derbyDir = "target/derby/testdb";
    File beforeDir = new File(derbyDir);
    if (beforeDir.exists()) {
      FileUtils.deleteDirectory(beforeDir);
    }

    conn = DriverManager.getConnection("jdbc:derby:" + derbyDir + ";create=true");
    Statement stmt = conn.createStatement();

    String script = FileUtils.readFileToString(
        new File("tools/db/initialize-for-dboperation-test.sql"), Charset.forName("UTF-8"));
    for (String queqy : script.split(";")) {
      if (!queqy.isEmpty()) {
        stmt.addBatch(queqy);
      }
    }
    stmt.executeBatch();

  }

  @Test
  public void test001() {
    test();
  }

  @Test
  public void test002() {
    try {
      test();
      fail();
    } catch (AssertionError e) {
      String[] errMsg = splitErrMsg(e.getMessage());
      assertThat(errMsg[1], is("io.sitoolkit.wt.infra.VerifyException: <br/>"));
      assertThat(errMsg[2], is("下記カラムの実測値が期待値と異なります"));
      assertThat(errMsg[4], is("<li>カラム[COL2] / 実測値[222] / 期待値[333]</li>"));
      assertThat(errMsg[5], is("<li>カラム[COL1] / 実測値[111] / 期待値[val1]</li>"));
    }
  }

  @Test
  public void test003() {
    try {
      test();
    } catch (AssertionError e) {
      String[] errMsg = splitErrMsg(e.getMessage());
      assertThat(errMsg[1], is("io.sitoolkit.wt.infra.TestException:"
          + " org.springframework.dao.EmptyResultDataAccessException: Incorrect result size: expected 1, actual 0"));
    }
  }

  @Test
  public void test004() {
    try {
      test();
      fail();
    } catch (AssertionError e) {
      String[] errMsg = splitErrMsg(e.getMessage());
      assertThat(errMsg[1], is("io.sitoolkit.wt.infra.TestException:"
          + " org.springframework.dao.IncorrectResultSizeDataAccessException: Incorrect result size: expected 1, actual 2"));
    }
  }

  @Test
  public void test005() {
    try {
      test();
      fail();
    } catch (AssertionError e) {
      String[] errMsg = splitErrMsg(e.getMessage());
      assertThat(errMsg[1], is(
          "io.sitoolkit.wt.infra.TestException: org.springframework.dao.InvalidDataAccessApiUsageException:"
              + " No value supplied for the SQL parameter 'COL2': No value registered for key 'COL2'"));
    }
  }

  @Test
  public void test006() {
    try {
      test();
      fail();
    } catch (AssertionError e) {
      String[] errMsg = splitErrMsg(e.getMessage());
      assertThat(errMsg[1], is(
          "io.sitoolkit.wt.infra.TestException: org.springframework.jdbc.BadSqlGrammarException: PreparedStatementCallback; bad SQL grammar [SELECT COL1, COL2 FROM TAB1 ID = ?]; nested exception is java.sql.SQLSyntaxErrorException: Syntax error: Encountered \"=\" at line 1, column 32."));
    }
  }

  @Test
  public void test007() {
    try {
      test();
      fail();
    } catch (AssertionError e) {
      String[] errMsg = splitErrMsg(e.getMessage());
      assertThat(errMsg[1],
          is("io.sitoolkit.wt.infra.TestException: java.io.FileNotFoundException: File '"
              + new File("aaa.sql").getAbsolutePath() + "' does not exist"));
    }
  }

  @Override
  protected String getTestScriptPath() {
    return "src/test/resources/selenium/DBVerifyOperationTestScript.csv";
  }

  @Override
  protected String getSheetName() {
    return "TestScript";
  }

  private String[] splitErrMsg(String str) {
    return str.replace("\t", "").replace("\r", "").split("\n");
  }

}
