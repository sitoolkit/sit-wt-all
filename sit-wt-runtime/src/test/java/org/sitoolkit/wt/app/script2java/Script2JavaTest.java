package org.sitoolkit.wt.app.script2java;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

public class Script2JavaTest {

    @Test
    public void testLoad() {
        Script2Java gen = new Script2Java();
        TestClass actual = new TestClass();
        gen.load(actual, new File(".", "testscript/a/b/c/ABCTestScript.xlsx"), "testscript");

        assertThat("スクリプトパス", actual.getScriptPath(), is("testscript/a/b/c/ABCTestScript.xlsx"));
        assertThat("テストクラス物理名", actual.getFileBase(), is("ABCTestScriptIT"));
        assertThat("テストクラスファイル拡張子", actual.getFileExt(), is("java"));
        assertThat("テストクラス出力ディレクトリ", actual.getOutDir(),
                is(FilenameUtils.separatorsToSystem("target/generated-test-sources/test/a/b/c/")));
        assertThat("テストクラスパッケージ名", actual.getPkg(), is("a.b.c"));
    }

}
