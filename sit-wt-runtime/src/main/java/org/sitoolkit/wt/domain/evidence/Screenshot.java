package org.sitoolkit.wt.domain.evidence;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.infra.SitPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Screenshot {

    private static final Logger LOG = LoggerFactory.getLogger(Screenshot.class);

    private File file;

    private File basedir;

    private int screenshotPaddingWidth;

    private int screenshotPaddingHeight;

    private boolean resize;

    private List<ElementPosition> positions = new ArrayList<ElementPosition>();

    public void addElementPosition(ElementPosition pos) {
        positions.add(pos);
    }

    public boolean flush() {

        if (file == null) {
            return false;
        }

        if (positions.isEmpty()) {
            return true;
        }

        if (!resize) {
            return true;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (ElementPosition pos : positions) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            maxX = Math.max(maxX, pos.getX() + pos.getW());
            maxY = Math.max(maxY, pos.getY() + pos.getH());
        }

        LOG.debug("elements:{} rectanble maxX:{}, minY:{}, maxX:{}, maxY:{}",
                new Object[] { positions.size(), maxX, minY, maxX, maxY });

        try {
            BufferedImage orgImg = ImageIO.read(file);
            int subX = Math.max(minX - screenshotPaddingWidth, 0);
            int subY = Math.max(minY - screenshotPaddingHeight, 0);
            int subW = Math.min((maxX - minX) + screenshotPaddingWidth * 2,
                    orgImg.getWidth() - subX);
            int subH = Math.min((maxY - minY) + screenshotPaddingHeight * 2,
                    orgImg.getHeight() - subY);

            LOG.debug("origonal image w:{}, h:{} sub image x:{}, y:{}, w:{}, h{}",
                    new Object[] { orgImg.getWidth(), orgImg.getHeight(), subX, subY, subW, subH });

            BufferedImage subImg = orgImg.getSubimage(subX, subY, subW, subH);
            ImageIO.write(subImg, "png", file);

            for (ElementPosition pos : positions) {
                pos.setX(pos.getX() - subX);
                pos.setY(pos.getY() - subY);
            }
        } catch (IOException e) {
            LOG.warn("スクリーンショットファイル{}のサイズ変更で例外が発生", file.getName(), e);
        }

        return true;
    }

    public void setFile(File imgDir, File file, String scriptName, String caseNo, String testStepNo,
            String itemName, String timing) {

        String screenshotFileName = buildScreenshotFileName(scriptName, caseNo, testStepNo,
                itemName, timing);
        File dstFile = new File(imgDir, screenshotFileName);

        try {

            if (dstFile.exists()) {
                dstFile = new File(imgDir, dstFile.getName() + "_" + System.currentTimeMillis());
            }
            FileUtils.moveFile(file, dstFile);
            this.file = dstFile;

            LOG.info("スクリーンショットを取得しました {}", dstFile.getAbsolutePath());

        } catch (IOException e) {
            LOG.warn("スクリーンショットファイルの移動に失敗しました", e);
        }
    }

    private String buildScreenshotFileName(String scriptName, String caseNo, String testStepNo,
            String itemName, String timing) {

        return StringUtils.join(new String[] { scriptName, caseNo, testStepNo, itemName, timing },
                "_") + ".png";
    }

    public String getFilePath() {
        return SitPathUtils.relatvePath(basedir, file);
    }

    public String getFileName() {
        return file.getName();
    }

    public List<ElementPosition> getPositions() {
        return positions;
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    public boolean isResize() {
        return resize;
    }

    public void setScreenshotPaddingWidth(int screenshotPaddingWidth) {
        this.screenshotPaddingWidth = screenshotPaddingWidth;
    }

    public void setScreenshotPaddingHeight(int screenshotPaddingHeight) {
        this.screenshotPaddingHeight = screenshotPaddingHeight;
    }

}
