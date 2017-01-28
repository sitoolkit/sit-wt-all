package org.sitoolkit.wt.app.test;

import java.util.ArrayList;
import java.util.List;

import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.domain.testscript.TestScriptDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestCaseReader {

    private static final Logger LOG = LoggerFactory.getLogger(TestCaseReader.class);

    public TestCaseReader() {
    }

    public static void main(String[] args) {
        System.exit(new TestCaseReader().execute(args));
    }

    public int execute(String[] args) {
        if (args.length < 1) {
            LOG.info("テストスクリプトを指定してください。");
            LOG.info(">java {} [scriptPath sheetName]", TestCaseReader.class.getName());
            return 1;
        }

        String sheetName = args.length < 2 ? "TestScript" : args[1];

        for (String caseNo : read(args[0], sheetName)) {
            System.out.println("Case No:" + caseNo);
        }

        return 0;
    }

    List<String> read(String scriptPath, String sheetName) {
        ConfigurableApplicationContext appCtx = new AnnotationConfigApplicationContext(
                TestCaseReaderConfig.class);
        TestScriptDao testScriptDao = appCtx.getBean(TestScriptDao.class);
        TestScript testScript = testScriptDao.load(scriptPath, sheetName, true);
        appCtx.close();
        return new ArrayList<>(testScript.getCaseNoMap().keySet());
    }

}
