package io.sitoolkit.wt.domain.evidence;

public abstract class DefaultScreenshotTaker extends ScreenshotTaker {

    public Screenshot get(ScreenshotTiming timing) {
        String dataStr = ScreenshotTiming.ON_DIALOG.equals(timing) ? getDialogAsData()
                : getAsData();

        return createScreenshot(timing, dataStr);
    }

    protected abstract String getAsData();

    protected abstract String getDialogAsData();

}
