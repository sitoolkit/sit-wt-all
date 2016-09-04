package org.sitoolkit.wt.domain.evidence;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.infra.template.TemplateEngineVelocityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenshotComparator {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotComparator.class);

    private static final String MASK_PREFIX = "mask_";

    public static final String SYSPROP_DRIVER_TYPE = "driver.type";

    private static final String UNMATCH_PREFIX = "unmatch_";

    DiffEvidenceGenerator generator = new DiffEvidenceGenerator();

    int openFileCount = 1;

    public static void main(String[] args) {
        ScreenshotComparator comparator = new ScreenshotComparator();
        comparator.compare(null, "default");
    }

    /**
     *
     * @param targetEvidenceDir
     *            対象エビデンスディレクトリ
     * @param browser
     *            対象エビデンスのテスト実行に使用したブラウザ
     * @see EvidenceUtils#baseEvidenceDir(String)
     */
    public void compare(String targetEvidenceDir, String browser) {

        File latestEvidenceDir = EvidenceUtils.targetEvidenceDir(targetEvidenceDir);
        if (latestEvidenceDir == null) {
            LOG.info("比較対象のエビデンスがありません");
            return;
        }

        File baseEvidenceImgDir = new File(EvidenceUtils.baseEvidenceDir(browser), "img");
        File targetEvidenceImgDir = new File(latestEvidenceDir, "img");
        LOG.info("スクリーンショットを比較します {} <-> {}", baseEvidenceImgDir, targetEvidenceImgDir);

        // スクリーンショット比較絞り込み
        // img内すべて → マスク済みファイルが存在するスクリーンショットは、オリジナルを比較対象から除外
        List<File> comparingTargetScreenshots = chooseTargetScreenshots(targetEvidenceImgDir);
        LOG.info("比較対象となるスクリーンショット {}", comparingTargetScreenshots);

        List<String> unmatchScreenshotNames = new ArrayList<>();

        for (File targetImg : comparingTargetScreenshots) {
            File baseImg = new File(baseEvidenceImgDir, targetImg.getName());

            if (baseImg.exists()) {

                if (!compareOneScreenshot(baseImg, targetImg, 10, 10)) {
                    unmatchScreenshotNames.add(targetImg.getName());
                }

            } else {
                LOG.info("基準スクリーンショットは存在しません {}", targetImg);
                continue;
            }
        }

        if (unmatchScreenshotNames.size() > 0) {

            // スクリーンショットdiff作成
            DiffEvidence diffEvidence = new DiffEvidence();
            diffEvidence.setUnmatchScreenshotNames(unmatchScreenshotNames);
            generator.setCompareEvidence(diffEvidence);
            generator.setTemplateEngine(new TemplateEngineVelocityImpl());
            generator.run(System.getProperty("main.browser"), true);

        }

    }

    private List<File> chooseTargetScreenshots(File dir) {

        String[] fileNameArray = dir.list();
        List<String> targetFileNameList = new ArrayList<>(Arrays.asList(fileNameArray));

        for (String str : fileNameArray) {
            if (str.startsWith(MASK_PREFIX)) {
                targetFileNameList.remove(str.replaceFirst(MASK_PREFIX, ""));
            }
        }

        List<File> targetFileList = new ArrayList<>();
        for (String s : targetFileNameList) {
            targetFileList.add(new File(dir, s));
        }

        return targetFileList;
    }

    /**
     * スクリーンショットが一致する場合にtrueを返します。 一致しない場合には差分スクリーンショットを生成します。
     *
     * @param baseImg
     *            基準スクリーンショット
     * @param targetImg
     *            比較対象スクリーンショット
     * @param rows
     *            分割行数
     * @param columns
     *            分割列数
     * @return スクリーンショットが一致する場合にtrue
     */
    public boolean compareOneScreenshot(File baseImg, File targetImg, int rows, int columns) {

        boolean match = true;
        BufferedImage[][] baseBfImg = splitImage(baseImg, rows, columns);
        BufferedImage[][] targetBfImg = splitImage(targetImg, rows, columns);

        BufferedImage[][] diffImg = new BufferedImage[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                byte[] baseImgBytes = toByteArray(baseBfImg[i][j]);
                byte[] targetImgBytes = toByteArray(targetBfImg[i][j]);

                if (Arrays.equals(baseImgBytes, targetImgBytes)) {
                    diffImg[i][j] = darken(targetBfImg[i][j]);
                } else {
                    match = false;
                    diffImg[i][j] = targetBfImg[i][j];
                }
            }
        }

        if (match) {
            LOG.info("スクリーンショットは基準と一致しました {}", targetImg.getName());
        } else {
            LOG.error("スクリーンショットは基準と一致しませんでした {}", targetImg.getName());
            writeDiffImg(targetImg, diffImg);
        }

        return match;
    }

    public BufferedImage[][] splitImage(File baseImg, int rows, int columns) {

        BufferedImage[][] splitImages = new BufferedImage[rows][columns];

        try {
            BufferedImage bufferedImage = ImageIO.read(baseImg);

            for (int i = 0; i < splitImages.length; i++) {
                for (int j = 0; j < splitImages[i].length; j++) {

                    int x = bufferedImage.getWidth() / columns * j;
                    int y = bufferedImage.getHeight() / rows * i;

                    int w;
                    if (j == splitImages[i].length - 1) {
                        w = bufferedImage.getWidth() - x;
                    } else {
                        w = bufferedImage.getWidth() / columns;
                    }

                    int h;
                    if (i == splitImages.length - 1) {
                        h = bufferedImage.getHeight() - y;
                    } else {
                        h = bufferedImage.getHeight() / rows;
                    }

                    splitImages[i][j] = bufferedImage.getSubimage(x, y, w, h);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return splitImages;
    }

    private void writeDiffImg(File targetImg, BufferedImage[][] bio) {

        try {
            BufferedImage img = ImageIO.read(targetImg);
            BufferedImage imgBase = new BufferedImage(img.getWidth(), img.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics g = imgBase.getGraphics();
            int x = bio[0][0].getWidth();
            int y = bio[0][0].getHeight();

            for (int i = 0; i < bio.length; i++) {
                for (int j = 0; j < bio[i].length; j++) {
                    g.drawImage(bio[i][j], x * j, y * i, null);
                }
            }

            String screenshotName = StringUtils.join(targetImg.getParent(), "/", UNMATCH_PREFIX,
                    targetImg.getName());

            File maskedImg = new File(screenshotName);
            ImageIO.write(imgBase, "png", maskedImg);
            LOG.info("差分スクリーンショットを生成しました {}", maskedImg);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private BufferedImage darken(BufferedImage bi) {
        Graphics2D g = bi.createGraphics();
        Color color = new Color(0.2f, 0.2f, 0.2f, 0.8f);
        g.setPaint(color);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        return bi;
    }

    private byte[] toByteArray(BufferedImage bi) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] byteArray = {};
        try {
            ImageIO.write(bi, "png", baos);
            baos.flush();
            byteArray = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public DiffEvidenceGenerator getBuilder() {
        return generator;
    }

    public void setBuilder(DiffEvidenceGenerator builder) {
        this.generator = builder;
    }

}
