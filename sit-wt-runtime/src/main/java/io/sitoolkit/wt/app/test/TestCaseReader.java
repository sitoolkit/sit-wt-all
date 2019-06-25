package io.sitoolkit.wt.app.test;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import io.sitoolkit.wt.domain.testscript.TestScript;
import io.sitoolkit.wt.domain.testscript.TestScriptDao;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class TestCaseReader {

  private static final SitLogger LOG = SitLoggerFactory.getLogger(TestCaseReader.class);

  @Resource
  TestScriptDao testScriptDao;

  public TestCaseReader() {
  }

  public static void main(String[] args) {
    System.exit(new TestCaseReader().execute(args));
  }

  public int execute(String[] args) {
    if (args.length < 1) {
      LOG.info("test.script.appoint");
      LOG.info("script.path", TestCaseReader.class.getName());
      return 1;
    }

    String sheetName = args.length < 2 ? "TestScript" : args[1];

    for (String caseNo : read(args[0], sheetName)) {
      System.out.println("Case No:" + caseNo);
    }

    return 0;
  }

  public List<String> getTestCase(String scriptPath) {
    return read(scriptPath, "TestScript");
  }

  List<String> read(String scriptPath, String sheetName) {
    TestScript testScript = testScriptDao.load(scriptPath, sheetName, true);
    return new ArrayList<>(testScript.getCaseNoMap().keySet());
  }

}
