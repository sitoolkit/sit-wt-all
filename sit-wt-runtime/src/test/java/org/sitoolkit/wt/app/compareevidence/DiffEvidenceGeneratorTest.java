package org.sitoolkit.wt.app.compareevidence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.infra.log.SitLogger;
import org.sitoolkit.wt.infra.log.SitLoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DiffEvidenceGeneratorTest {

    private static final SitLogger LOG = SitLoggerFactory
            .getLogger(DiffEvidenceGeneratorTest.class);

    DiffEvidenceGenerator generator;

    EvidenceDir baseDir = EvidenceDir.getInstance("compareevidence/base");

    EvidenceDir targetDir = EvidenceDir
            .getInstance("target/evidence_" + RandomStringUtils.randomNumeric(4));

    String evidencePath = targetDir.getDir().getPath();

    @Before
    public void setUp() throws IOException {
        ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                DiffEvidenceGeneratorConfig.class);
        generator = appCtx.getBean(DiffEvidenceGenerator.class);
        FileUtils.copyDirectoryToDirectory(new File("compareevidence/target/css"),
                targetDir.getDir());
        FileUtils.copyDirectoryToDirectory(new File("compareevidence/target/img"),
                targetDir.getDir());
        FileUtils.copyFileToDirectory(new File("compareevidence/target/sit-wt.properties"),
                targetDir.getDir());
        System.setProperty("evidence.base", "compareevidence/base");
    }

    @Test
    public void testGenerateWithoutScreenshot() throws IOException {

        FileUtils.copyFileToDirectory(
                new File("compareevidence/target/ABCTestScript.xlsx_001.html"), targetDir.getDir());

        boolean result = generator.generate(baseDir, targetDir, false);

        assertThat("evidence comparing result", result, is(true));
        assertThat("マスク版エビデンス生成",
                new File(evidencePath, "mask_ABCTestScript.xlsx_001.html").exists(), is(false));
        assertThat("比較エビデンス生成", new File(evidencePath, "comp_ABCTestScript.xlsx_001.html").exists(),
                is(true));
        assertThat("比較（マスク版）エビデンス生成",
                new File(evidencePath, "comp_mask_ABCTestScript.xlsx_001.html").exists(),
                is(false));
        assertThat("不一致スクリーンショットに対する比較エビデンス未生成",
                new File(evidencePath, "comp_ng_ABCTestScript.xlsx_001.html").exists(), is(false));

    }

    @Test
    public void testGenerateWithScreenshotMatch() throws IOException {

        FileUtils.copyFileToDirectory(
                new File("compareevidence/target/ABCTestScript.xlsx_002.html"), targetDir.getDir());

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(true));
        assertThat("マスク版エビデンス生成",
                new File(evidencePath, "mask_ABCTestScript.xlsx_002.html").exists(), is(false));
        assertThat("比較エビデンス生成", new File(evidencePath, "comp_ABCTestScript.xlsx_002.html").exists(),
                is(true));
        assertThat("比較（マスク版）エビデンス生成",
                new File(evidencePath, "comp_mask_ABCTestScript.xlsx_002.html").exists(),
                is(false));
        assertThat("不一致スクリーンショットに対する比較エビデンス未生成",
                new File(evidencePath, "comp_ng_ABCTestScript.xlsx_002.html").exists(), is(false));

    }

    @Test
    public void testGenerateWithScreenshotUnmatch() throws IOException {

        FileUtils.copyFileToDirectory(
                new File("compareevidence/target/ABCTestScript.xlsx_003.html"), targetDir.getDir());

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(false));
        assertThat("マスク版エビデンス生成",
                new File(evidencePath, "mask_ABCTestScript.xlsx_003.html").exists(), is(false));
        assertThat("比較エビデンス生成", new File(evidencePath, "comp_ABCTestScript.xlsx_003.html").exists(),
                is(true));
        assertThat("比較（マスク版）エビデンス生成",
                new File(evidencePath, "comp_mask_ABCTestScript.xlsx_003.html").exists(),
                is(false));
        assertThat("不一致スクリーンショットに対する比較エビデンス未生成",
                new File(evidencePath, "comp_ng_ABCTestScript.xlsx_003.html").exists(), is(true));

    }

    @Test
    public void testGenerateWithScreenshotWithMaskMatch() throws IOException {

        FileUtils.copyFileToDirectory(
                new File("compareevidence/target/ABCTestScript.xlsx_004.html"), targetDir.getDir());

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(true));
        assertThat("マスク版エビデンス生成",
                new File(evidencePath, "mask_ABCTestScript.xlsx_004.html").exists(), is(true));
        assertThat("比較エビデンス生成", new File(evidencePath, "comp_ABCTestScript.xlsx_004.html").exists(),
                is(true));
        assertThat("比較（マスク版）エビデンス生成",
                new File(evidencePath, "comp_mask_ABCTestScript.xlsx_004.html").exists(), is(true));
        assertThat("不一致スクリーンショットに対する比較エビデンス未生成",
                new File(evidencePath, "comp_ng_ABCTestScript.xlsx_004.html").exists(), is(false));

    }

    @Test
    public void testGenerateWithScreenshotWithMaskMatchUnmach() throws IOException {

        FileUtils.copyFileToDirectory(
                new File("compareevidence/target/ABCTestScript.xlsx_005.html"), targetDir.getDir());

        boolean result = generator.generate(baseDir, targetDir, true);

        assertThat("evidence comparing result", result, is(false));
        assertThat("マスク版エビデンス生成",
                new File(evidencePath, "mask_ABCTestScript.xlsx_005.html").exists(), is(true));
        assertThat("比較エビデンス生成", new File(evidencePath, "comp_ABCTestScript.xlsx_005.html").exists(),
                is(true));
        assertThat("比較（マスク版）エビデンス生成",
                new File(evidencePath, "comp_ABCTestScript.xlsx_005.html").exists(), is(true));
        assertThat("スクリーンショット比較NGエビデンス生成",
                new File(evidencePath, "comp_ng_ABCTestScript.xlsx_005.html").exists(), is(true));

    }

}
