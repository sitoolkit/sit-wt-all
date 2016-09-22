package org.sitoolkit.wt.app.compareevidence;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;

public class DiffEvidenceGeneratorTest {

    DiffEvidenceGenerator generator = new DiffEvidenceGenerator();

    /**
     * TODO このディレクトリ以下にテスト用のエビデンスファイル群を格納
     */
    EvidenceDir baseDir = EvidenceDir.getInstance("compareevidence/base");

    /**
     * TODO このディレクトリ以下にテスト用のエビデンスファイル群を格納
     */
    EvidenceDir targetDir = EvidenceDir
            .getInstance("target/evidence_" + RandomStringUtils.randomNumeric(4));

    @Before
    public void setup() throws IOException {
        FileUtils.copyDirectory(new File("compareevidence/target"), targetDir.getDir());
    }

    @Test
    public void testGenerateWithoutScreenshot() {
        boolean result = generator.generate(baseDir, targetDir, false);

        assertThat("evidence comparing result", result, is(false));

        // TODO 比較エビデンスが生成されることを確認
    }

    @Test
    public void testGenerateWithScreenshotMatch() {
        // TODO テストコンディションを満たす様にファイルを操作

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(false));

        // TODO 比較エビデンスが生成されることを確認
    }

    @Test
    public void testGenerateWithScreenshotUnmatch() {
        // TODO テストコンディションを満たす様にファイルを操作

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(true));

        // TODO 比較エビデンスが生成されることを確認

    }

    @Test
    public void testGenerateWithScreenshotWithMaskMatch() {
        // TODO テストコンディションを満たす様にファイルを操作

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(false));

        // TODO 比較エビデンスが生成されることを確認
    }

    @Test
    public void testGenerateWithScreenshotWithMaskMatchUnmach() {
        // TODO テストコンディションを満たす様にファイルを操作

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(false));

        // TODO 比較エビデンスが生成されることを確認
    }

}
