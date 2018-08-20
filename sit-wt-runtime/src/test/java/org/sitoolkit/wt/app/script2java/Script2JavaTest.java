package org.sitoolkit.wt.app.script2java;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.sitoolkit.wt.app.config.ExtConfig;
import org.sitoolkit.wt.domain.testscript.TestScript;
import org.sitoolkit.wt.infra.TestException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Script2JavaTest {

    @Test
    public void testLoad() {
        TestClass actual = loadScript("testscript/a/b/c/ABCTestScript.csv", "testscript");

        assertThat("スクリプトパス", actual.getScriptPath(), is("testscript/a/b/c/ABCTestScript.csv"));
        assertThat("テストクラス物理名", actual.getFileBase(), is("ABCTestScriptIT"));
        assertThat("テストクラスファイル拡張子", actual.getFileExt(), is("java"));
        assertThat("テストクラス出力ディレクトリ", actual.getOutDir(),
                is(FilenameUtils.separatorsToSystem("target/generated-test-sources/test/a/b/c/")));
        assertThat("テストクラスパッケージ名", actual.getPkg(), is("a.b.c"));
    }

    @Test
    public void testMultiByteScript() {
        TestClass actual = loadScript("src/test/resources/テスト-スクリプト(サンプル).csv",
                "src/test/resources");

        assertThat("スクリプトパス", actual.getScriptPath(), is("src/test/resources/テスト-スクリプト(サンプル).csv"));
        assertThat("テストクラス物理名", actual.getFileBase(), is("テスト_スクリプト_サンプル_IT"));
        assertThat("テストクラスファイル拡張子", actual.getFileExt(), is("java"));
        assertThat("テストクラス出力ディレクトリ", actual.getOutDir(),
                is(FilenameUtils.separatorsToSystem("target/generated-test-sources/test/")));
        assertNull("テストクラスパッケージ名", actual.getPkg());
        assertThat("テストケース数", actual.getCaseNos().size(), is(8));

        String testCaseName = "テストケース名";
        for (String caseNo : actual.getCaseNos()) {
            String sanitizedCaseNo = actual.sanitizeFunctionName(caseNo);
            switch (caseNo) {
                case "スラッ/シュ":
                    assertThat(testCaseName, sanitizedCaseNo, is("スラッ_シュ"));
                    break;
                case "バック\\スラッシュ":
                    assertThat(testCaseName, sanitizedCaseNo, is("バック_スラッシュ"));
                    break;
                case "コロ:ン":
                    assertThat(testCaseName, sanitizedCaseNo, is("コロ_ン"));
                    break;
                case "アスタ*リスク":
                    assertThat(testCaseName, sanitizedCaseNo, is("アスタ_リスク"));
                    break;
                case "ダブル\"クォート":
                    assertThat(testCaseName, sanitizedCaseNo, is("ダブル_クォート"));
                    break;
                case "大>なり":
                    assertThat(testCaseName, sanitizedCaseNo, is("大_なり"));
                    break;
                case "小<なり":
                    assertThat(testCaseName, sanitizedCaseNo, is("小_なり"));
                    break;
                case "パイ|プ":
                    assertThat(testCaseName, sanitizedCaseNo, is("パイ_プ"));
                    break;
                default:
                    throw new TestException("undefined CaseNo found : " + caseNo);
            }
        }
    }

    private TestClass loadScript(String testscript, String scriptDir) {
        ApplicationContext appCtx = new AnnotationConfigApplicationContext(Script2JavaConfig.class,
                ExtConfig.class);
        Script2Java gen = appCtx.getBean(Script2Java.class);

        TestClass actual = new TestClass();
        gen.load(actual, new File(".", testscript), scriptDir);

        File scriptFile = new File(testscript);
        if (scriptFile.exists()) {
            TestScript testScript = gen.getDao().load(testscript, actual.getSheetName(), true);
            actual.getCaseNos().addAll(testScript.getCaseNoMap().keySet());
        }

        return actual;
    }
}
