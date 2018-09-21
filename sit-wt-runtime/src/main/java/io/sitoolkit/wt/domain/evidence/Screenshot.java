package io.sitoolkit.wt.domain.evidence;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

public class Screenshot {

    private static final SitLogger LOG = SitLoggerFactory.getLogger(Screenshot.class);

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private File file;

    private String filePath;

    private int screenshotPaddingWidth;

    private int screenshotPaddingHeight;

    private int width;

    private boolean resize;

    private boolean resizing;

    private List<ElementPosition> positions = new ArrayList<ElementPosition>();

    private ScreenshotTiming timing;

    private String errorMesage;

    public void addElementPosition(ElementPosition pos) {
        positions.add(pos);
    }

    public void clearElementPosition() {
        positions.clear();
    }

    public Future<?> resize() {

        if (file == null) {
            return null;
        }

        if (positions.isEmpty()) {
            return null;
        }

        if (!resize) {
            return EXECUTOR.submit(() -> {
                try {
                    width = ImageIO.read(file).getWidth();
                } catch (IOException e) {
                    LOG.warn("warn", e);
                }
            });
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

        LOG.debug("element.info", new Object[] { positions.size(), maxX, minY, maxX, maxY });

        resizing = true;
        return EXECUTOR.submit(new Resizer(minX, minY, maxX, maxY));
    }

    private class Resizer implements Runnable {

        private int minX;
        private int minY;
        private int maxX;
        private int maxY;

        public Resizer(int minX, int minY, int maxX, int maxY) {
            super();
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        @Override
        public void run() {
            try {
                BufferedImage orgImg = ImageIO.read(file);
                int subX = Math.max(minX - screenshotPaddingWidth, 0);
                int subY = Math.max(minY - screenshotPaddingHeight, 0);
                int subW = Math.min((maxX - minX) + screenshotPaddingWidth * 2,
                        orgImg.getWidth() - subX);
                int subH = Math.min((maxY - minY) + screenshotPaddingHeight * 2,
                        orgImg.getHeight() - subY);

                for (ElementPosition pos : positions) {
                    pos.setX(pos.getX() - subX);
                    pos.setY(pos.getY() - subY);
                }

                BufferedImage subImg = orgImg.getSubimage(subX, subY, subW, subH);
                width = subImg.getWidth();
                ImageIO.write(subImg, "png", file);

                LOG.debug("origonal.image.info", new Object[] { orgImg.getWidth(),
                        orgImg.getHeight(), subX, subY, subW, subH, file.getAbsolutePath() });

            } catch (IOException e) {
                LOG.error("screenshot.resize.error", file.getName(), e);
            } finally {
                resizing = false;
            }

        }

    }

    public String getFileName() {

        return file == null ? "" : file.getName();
    }

    public List<ElementPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<ElementPosition> positions) {
        this.positions = positions;
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

    public void setFile(File file) {
        this.file = file;
    }

    public ScreenshotTiming getTiming() {
        return timing;
    }

    public void setTiming(ScreenshotTiming timing) {
        this.timing = timing;
    }

    public File getFile() {
        return file;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public synchronized boolean isResizing() {
        return resizing;
    }

    public String getErrorMesage() {
        return errorMesage;
    }

    public void setErrorMesage(String errorMesage) {
        this.errorMesage = errorMesage;
    }

    public int getWidth() {
        return width;
    }

}
