package org.sitoolkit.wt.domain.evidence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;

public class Evidence {

    private String scriptName;

    private String caseNo;

    private List<LogRecord> records = new ArrayList<LogRecord>();

    private List<ElementPosition> currentPositions = new ArrayList<>();

    private Screenshot currentScreenshot;

    private Future<?> screenshotResizeFuture;

    public void addLogRecord(LogRecord log) {
        records.add(log);
    }

    public void addElementPosition(ElementPosition pos) {
        currentPositions.add(pos);
    }

    public void addScreenshot(Screenshot screenshot, String resize) {

        LogRecord record = new LogRecord();
        records.add(record);
        currentScreenshot = screenshot;

        if (StringUtils.isNotEmpty(screenshot.getErrorMesage())) {
            record.setLog("スクリーンショットの取得に失敗しました " + screenshot.getErrorMesage());
            return;
        }

        record.setScreenshot(currentScreenshot);

        if (screenshot.isResize()) {
            if (StringUtils.contains(resize, "全")) {
                screenshot.setResize(false);
            }
        } else {
            if (StringUtils.contains(resize, "縮")) {
                screenshot.setResize(true);
            }
        }

    }

    public void commitScreenshot() {

        if (currentScreenshot == null) {
            return;
        }

        if (currentScreenshot.getFile() != null
                && !ScreenshotTiming.ON_DIALOG.equals(currentScreenshot.getTiming())) {
            currentScreenshot.setPositions(currentPositions);
            screenshotResizeFuture = currentScreenshot.resize();
        }

        currentPositions = new ArrayList<>();
        currentScreenshot = null;

    }

    public boolean hasError() {
        for (LogRecord logRecord : records) {
            if (LogLevelVo.ERROR.equals(logRecord.getLogLevel())) {
                return true;
            }
        }
        return false;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public List<LogRecord> getRecords() {
        return records;
    }

    public Screenshot getCurrentScreenshot() {
        return currentScreenshot;
    }

    public Future<?> getScreenshotResizeFuture() {
        return screenshotResizeFuture;
    }

}
