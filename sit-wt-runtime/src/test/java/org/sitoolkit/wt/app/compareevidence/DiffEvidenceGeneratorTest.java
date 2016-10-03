package org.sitoolkit.wt.app.compareevidence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DiffEvidenceGeneratorTest {

    private static final Logger LOG = LoggerFactory.getLogger(DiffEvidenceGeneratorTest.class);

    DiffEvidenceGenerator generator;

    EvidenceDir baseDir = EvidenceDir.getInstance("compareevidence/base");

    EvidenceDir targetDir = EvidenceDir
            .getInstance("target/evidence_" + RandomStringUtils.randomNumeric(4));

    EvidenceDir unmatchImgDir = EvidenceDir.getInstance("compareevidence/unmatch");

    String evidencePath = targetDir.getDir().getPath();

    String unmatchImgPath = unmatchImgDir.getDir().getPath();

    @Before
    public void setup() throws IOException {
        ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                DiffEvidenceGeneratorConfig.class);
        generator = appCtx.getBean(DiffEvidenceGenerator.class);
        FileUtils.copyDirectory(new File("compareevidence/target"), targetDir.getDir());
        System.setProperty("baseEvidence", "compareevidence/base");
    }

    @Test
    public void testGenerateWithoutScreenshot() {

        // テストコンディションを満たす様にファイルを操作
        removeEvidenceExceptFor("TestScript1_WithoutScreenshot.xlsx_001.html", targetDir);

        boolean result = generator.generate(baseDir, targetDir, false);

        assertThat("スクリーンショット比較結果", result, is(true));

        // 比較エビデンスが生成されることを確認
        assertThat("比較エビデンス生成",
                new File(StringUtils.join(new String[] { evidencePath,
                        "comp_TestScript1_WithoutScreenshot.xlsx_001.html" }, "/")).exists(),
                is(true));
        assertThat("不一致スクリーンショットに対する比較エビデンス未生成",
                new File(
                        StringUtils.join(
                                new String[] { evidencePath,
                                        "comp_ng_TestScript1_WithoutScreenshot.xlsx_001.html" },
                                "/")).exists(),
                is(false));

    }

    @Test
    public void testGenerateWithScreenshotMatch() throws IOException {
        // テストコンディションを満たす様にファイルを操作
        // 無関係なhtmlを削除
        removeEvidenceExceptFor("TestScript2_WithScreenshotMatch.xlsx_001.html", targetDir);

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(true));

        // 比較エビデンスが生成されることを確認
        assertThat("比較エビデンス生成",
                new File(
                        StringUtils.join(
                                new String[] { evidencePath,
                                        "comp_TestScript2_WithScreenshotMatch.xlsx_001.html" },
                                "/")).exists(),
                is(true));
        assertThat("不一致スクリーンショットに対する比較エビデンス未生成",
                new File(
                        StringUtils.join(
                                new String[] { evidencePath,
                                        "comp_ng_TestScript2_WithScreenshotMatch.xlsx_001.html" },
                                "/")).exists(),
                is(false));

    }

    @Test
    public void testGenerateWithScreenshotUnmatch() throws IOException {

        // テストコンディションを満たす様にファイルを操作
        removeEvidenceExceptFor("TestScript3_WithScreenshotUnmatch.xlsx_001.html", targetDir);
        copyUnmatchScreenshot(
                "TestScript3_WithScreenshotUnmatch.xlsx_001_10_利用規約リンク_BEFORE_OPERATION.png");

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(false));

        // 比較エビデンスが生成されることを確認
        assertThat("比較エビデンス生成",
                new File(
                        StringUtils.join(
                                new String[] { evidencePath,
                                        "comp_TestScript3_WithScreenshotUnmatch.xlsx_001.html" },
                                "/")).exists(),
                is(true));
        assertThat("不一致スクリーンショットに対する比較エビデンス生成",
                new File(
                        StringUtils.join(
                                new String[] { evidencePath,
                                        "comp_ng_TestScript3_WithScreenshotUnmatch.xlsx_001.html" },
                                "/")).exists(),
                is(true));

    }

    @Test
    public void testGenerateWithScreenshotWithMaskMatch() {

        removeEvidenceExceptFor("TestScript4_WithScreenshotWithMaskMatch.xlsx_001.html", targetDir);

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(true));

        // 比較エビデンスが生成されることを確認
        assertThat("比較エビデンス生成",
                new File(StringUtils.join(
                        new String[] { evidencePath,
                                "comp_TestScript4_WithScreenshotWithMaskMatch.xlsx_001.html" },
                        "/")).exists(),
                is(true));
        assertThat("不一致スクリーンショットに対する比較エビデンス未生成",
                new File(StringUtils.join(
                        new String[] { evidencePath,
                                "comp_ng_TestScript4_WithScreenshotWithMaskMatch.xlsx_001.html" },
                        "/")).exists(),
                is(false));
    }

    @Test
    public void testGenerateWithScreenshotWithMaskMatchUnmach() throws IOException {
        // テストコンディションを満たす様にファイルを操作
        removeEvidenceExceptFor("TestScript5_WithScreenshotWithMaskMatchUnmach.xlsx_001.html",
                targetDir);
        copyUnmatchScreenshot(
                "TestScript5_WithScreenshotWithMaskMatchUnmach.xlsx_001_10_利用規約リンク_BEFORE_OPERATION.png");
        copyUnmatchScreenshot(
                "mask_TestScript5_WithScreenshotWithMaskMatchUnmach.xlsx_001_1_開始URL_AFTER_OPERATION.png");

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(false));

        // 比較エビデンスが生成されることを確認
        assertThat("比較エビデンス生成",
                new File(StringUtils.join(
                        new String[] { evidencePath,
                                "comp_TestScript5_WithScreenshotWithMaskMatchUnmach.xlsx_001.html" },
                        "/")).exists(),
                is(true));
        assertThat("不一致スクリーンショットに対する比較エビデンス生成",
                new File(StringUtils.join(
                        new String[] { evidencePath,
                                "comp_ng_TestScript5_WithScreenshotWithMaskMatchUnmach.xlsx_001.html" },
                        "/")).exists(),
                is(true));

    }

    private void removeEvidenceExceptFor(String targetEvidenceName, EvidenceDir targetDir) {

        for (File f : targetDir.getEvidenceFiles()) {
            if (f.getName().equals(targetEvidenceName)) {
                continue;
            }
            LOG.info("検証対象外のエビデンスを削除します {}", f.getName());
            f.delete();
        }
    }

    private void copyUnmatchScreenshot(String ssName) throws IOException {

        File srcFile = new File(StringUtils.join(new String[] { unmatchImgPath, ssName }, "/"));
        File dstFile = new File(StringUtils.join(new String[] { evidencePath, ssName }, "/img/"));

        LOG.info("基準と一致しないテスト用スクリーンショットをコピーします {}, {} -> {}", srcFile.getName(),
                srcFile.getParent(), dstFile.getParent());
        FileUtils.copyFile(srcFile, dstFile);

    }

}
