package org.sitoolkit.wt.app.compareevidence;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ScreenshotComparator {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotComparator.class);

    int openFileCount = 1;

    public static void staticExecute(EvidenceDir baseDir, EvidenceDir targetDir) {

        ApplicationContext appCtx = new AnnotationConfigApplicationContext(
                ScreenshotComparatorConfig.class);
        ScreenshotComparator comparator = appCtx.getBean(ScreenshotComparator.class);

        for (File evidenceFile : targetDir.getEvidenceFiles()) {
            comparator.compare(baseDir, targetDir, evidenceFile);
        }
    }

    /**
     * スクリーンショットを比較します。
     *
     * @param baseDir
     *            基準エビデンスディレクトリ
     * @param targetDir
     *            比較対象エビデンスディレクトリ
     * @param evidenceFile
     *            比較対象エビデンス
     * @return 比較対象エビデンスの全スクリーンショットが基準と一致する場合にtrue
     */
    public boolean compare(EvidenceDir baseDir, EvidenceDir targetDir, File evidenceFile) {

        LOG.info("スクリーンショットを比較します {} {} <-> {}",
                new Object[] { evidenceFile, baseDir.getDir(), targetDir.getDir() });

        if (!baseDir.exists()) {
            LOG.info("基準エビデンスディレクトリが存在しません {}", baseDir.getDir().getPath());
            return false;
        }

        Map<String, File> baseSsMap = baseDir.getScreenshotFilesAsMap(evidenceFile.getName());
        boolean match = true;

        for (Entry<String, File> targetEntry : targetDir
                .getScreenshotFilesAsMap(evidenceFile.getName()).entrySet()) {

            if (baseSsMap.get(EvidenceDir.toMaskSsName(targetEntry.getKey())) != null) {
                continue;
            }

            File baseSs = baseSsMap.get(targetEntry.getKey());
            File targetSs = targetEntry.getValue();

            if (baseSs == null) {
                LOG.warn("基準エビデンスディレクトリに存在しないスクリーンショットです {}", targetEntry.getKey());
                match = false;
                continue;
            }

            match &= compareOneScreenshot(baseSs, targetSs, 10, 10);
        }

        return match;
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

                    int posX = bufferedImage.getWidth() / columns * j;
                    int posY = bufferedImage.getHeight() / rows * i;

                    int width;
                    if (j == splitImages[i].length - 1) {
                        width = bufferedImage.getWidth() - posX;
                    } else {
                        width = bufferedImage.getWidth() / columns;
                    }

                    int height;
                    if (i == splitImages.length - 1) {
                        height = bufferedImage.getHeight() - posY;
                    } else {
                        height = bufferedImage.getHeight() / rows;
                    }

                    splitImages[i][j] = bufferedImage.getSubimage(posX, posY, width, height);

                }
            }

        } catch (IOException e) {
            LOG.error("スクリーンショットの分割処理で例外が発生しました", e);
        }
        return splitImages;
    }

    private void writeDiffImg(File targetImg, BufferedImage[][] bio) {

        try {
            BufferedImage img = ImageIO.read(targetImg);
            BufferedImage imgBase = new BufferedImage(img.getWidth(), img.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics graphics = imgBase.getGraphics();
            int posX = bio[0][0].getWidth();
            int posY = bio[0][0].getHeight();

            for (int i = 0; i < bio.length; i++) {
                for (int j = 0; j < bio[i].length; j++) {
                    graphics.drawImage(bio[i][j], posX * j, posY * i, null);
                }
            }

            File maskedImg = new File(targetImg.getParent(),
                    EvidenceDir.toUnmatchSsName(targetImg.getName()));
            ImageIO.write(imgBase, "png", maskedImg);
            LOG.info("差分スクリーンショットを生成しました {}", maskedImg);

        } catch (IOException e) {
            LOG.error("差分スクリーンショット生成処理で例外が発生しました", e);
        }

    }

    private BufferedImage darken(BufferedImage bi) {
        Graphics2D g2d = bi.createGraphics();
        Color color = new Color(0.2f, 0.2f, 0.2f, 0.8f);
        g2d.setPaint(color);
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
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
            LOG.error("バイト配列変換処理で例外が発生しました", e);
        }
        return byteArray;
    }

}
